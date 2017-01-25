package com.daquexian.chaoli.forum;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.daquexian.chaoli.forum.meta.NightModeHelper;

/**
 * Created by jianhao on 16-8-25.
 */
public class ChaoliApplication extends Application {
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        ChaoliApplication.appContext = getApplicationContext();
        if (NightModeHelper.isDay()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static SharedPreferences getSp(String name, int mode) {
        return appContext.getSharedPreferences(name, mode);
    }
}
