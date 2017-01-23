package com.daquexian.chaoli.forum;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.daquexian.chaoli.forum.meta.NightModeHelper;

/**
 * Created by jianhao on 16-8-25.
 */
public class ChaoliApplication extends Application {
    private static Context appContext;
    private static NightModeHelper nightModeHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        ChaoliApplication.appContext = getApplicationContext();
        nightModeHelper = new NightModeHelper(getApplicationContext());
        if (nightModeHelper.IsDay()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static NightModeHelper getDayNightHelper(){
        return nightModeHelper;
    }
}
