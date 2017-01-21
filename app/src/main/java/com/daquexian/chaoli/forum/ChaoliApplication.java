package com.daquexian.chaoli.forum;

import android.app.Application;
import android.content.Context;

import com.daquexian.chaoli.forum.meta.DayNightHelper;

/**
 * Created by jianhao on 16-8-25.
 */
public class ChaoliApplication extends Application {
    private static Context appContext;
    private static DayNightHelper dayNightHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        ChaoliApplication.appContext = getApplicationContext();
        dayNightHelper = new DayNightHelper(getApplicationContext());
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static DayNightHelper getDayNightHelper(){
        return dayNightHelper;
    }
}
