package com.daquexian.chaoli.forum;

import android.app.Application;
import android.content.Context;

/**
 * Created by jianhao on 16-8-25.
 */
public class ChaoliApplication extends Application {
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        ChaoliApplication.appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
