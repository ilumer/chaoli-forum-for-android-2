package com.daquexian.chaoli.forum;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.daquexian.chaoli.forum.meta.Constants;
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

    /**
     * get the app-wide shared preference.
     * @return app-wide shared preference
     */
    public static SharedPreferences getSp() {
        return appContext.getSharedPreferences(Constants.APP_NAME, MODE_PRIVATE);
    }
}
