package com.pactera.tesko;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pactera.Util.MySharedPreference;
import com.pactera.Util.PrefKeys;

public class OtpActivity extends AppCompatActivity {

    MySharedPreference preference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        preference = new MySharedPreference(this);
        EditText etOtp = findViewById(R.id.et_otp);
        findViewById(R.id.btn_submit).setOnClickListener(view -> {
            if(etOtp.getText().toString().equalsIgnoreCase("12345")){
                if(preference.getPref(PrefKeys.StoreType).equalsIgnoreCase("Express") ){
                    etOtp.setText("");
                    preference.setPref(PrefKeys.QUEUE_NAME,"Q1");
                    //preference.setPref(PrefKeys.StoreType,storeTypes[2]);
                    startActivity(new Intent(OtpActivity.this, QueueActivity1.class));
                }else{
                    startActivity(new Intent(OtpActivity.this, MainActivity.class));
                }
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Incorrect Otp",Toast.LENGTH_SHORT).show();
                etOtp.setText("");
            }
        });
    }
}
