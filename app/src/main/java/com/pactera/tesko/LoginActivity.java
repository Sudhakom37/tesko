package com.pactera.tesko;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.pactera.Util.MySharedPreference;
import com.pactera.Util.PrefKeys;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Button submit;
    EditText name;
    EditText pwd;
    boolean isDenied;
    Spinner sp_store_type;
    MySharedPreference preference;
    String[] storeTypes = {"Select Store","Hyper","Express","Extra","Department","Talad"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        submit = findViewById(R.id.btn_submit);
        name = findViewById(R.id.et_name);
        pwd = findViewById(R.id.et_pwd);
        sp_store_type = findViewById(R.id.sp_store_type);
        requestPermissions();
        preference = new MySharedPreference(this);
        setTimeSpinner();




    }

    void setTimeSpinner() {

        sp_store_type.setOnItemSelectedListener(this);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, storeTypes);
        timeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp_store_type.setAdapter(timeAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position!=0){
            preference.setPref("StoreType",storeTypes[2]);

        }else{
            preference.setPref("StoreType",storeTypes[position]);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE


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
                if(preference.getPref(PrefKeys.StoreType).equalsIgnoreCase(storeTypes[0])){
                    Toast.makeText(this, "Please Select Store", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(LoginActivity.this, OtpActivity.class));

            } else {
                Toast.makeText(LoginActivity.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
