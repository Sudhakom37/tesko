package com.pactera.tesko;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {


    Button submit;
    EditText name;
    EditText pwd;
    boolean isDenied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        submit = findViewById(R.id.btn_submit);
        name = findViewById(R.id.et_name);
        pwd = findViewById(R.id.et_pwd);
        requestPermissions();


    }

    public void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,


        };
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                doLogin();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Toast.makeText(context, "Permissions Are required To Continue", Toast.LENGTH_LONG).show();
                if (deniedPermissions.size() > 0) {
                    isDenied = true;

                    requestPermissions();
                }
            }
        });
    }

    void doLogin() {
        submit.setOnClickListener(v -> {

            if (name.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pwd.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.getText().toString().equals("Tesco_Express_Bangkok_XXXX23") && pwd.getText().toString().equals("admin")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            } else {
                Toast.makeText(LoginActivity.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
