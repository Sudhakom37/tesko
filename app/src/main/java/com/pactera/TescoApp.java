package com.pactera;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;


import com.pactera.tesko.BuildConfig;
import com.pushbots.push.Pushbots;

import java.io.IOException;
import java.io.OutputStreamWriter;


/**
 * Created by sahil on 15/12/16.
 */

public class TescoApp extends Application {
    public static final String APP_KEYVALUESTORE_KEY = "BHIMPreferences";
    public static final String NPCICL_KEYVALUESTORE_KEY = "NPCI";
    public static final String INTERNAL_STORAGE_DIR = "bhim";
    public static final String UI_BROADCAST_RECEIVER_NAME = BuildConfig.APPLICATION_ID+".uibroadcastreceiver";
    public static final String ALARM_RECEIVER_NAME = BuildConfig.APPLICATION_ID+".alarmReceiver";
    public static boolean APP_DEBUGGABLE = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Pushbots.sharedInstance().init(this);
    }


}
