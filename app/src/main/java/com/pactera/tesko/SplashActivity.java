package com.pactera.tesko;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;


/**
 * Splash is a first activity when user open the app
 */
@Keep
public class SplashActivity extends AppCompatActivity {
    //splash time
    private static int SPLASH_TIME_OUT_CACHE = 2000;
    private boolean isLogin, isMobileVerified;
    private String userName;
    private String password;
    public static boolean isComeFromStack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchActivity();
            }
        }, SPLASH_TIME_OUT_CACHE);

    }

    /***
     * Go to home or login or Digit page
     */
    private void switchActivity() {
        Intent intent = null;
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
