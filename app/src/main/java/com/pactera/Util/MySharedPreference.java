package com.pactera.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {

    SharedPreferences mainSharedPreference;

    public MySharedPreference(Context context){

        mainSharedPreference = context.getSharedPreferences("main_activity", Context.MODE_PRIVATE);
    }

    public String getPref(String key){
        return  mainSharedPreference.getString(key, "Q1");
    }

    public void setPref(String key, String value){
        SharedPreferences.Editor e = mainSharedPreference.edit();
        e.putString(key, value);
        e.apply();
    }

    public void clearAllPref(){
        SharedPreferences.Editor e = mainSharedPreference.edit();
        e.clear().apply();
    }

}