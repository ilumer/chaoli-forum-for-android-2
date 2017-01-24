package com.daquexian.chaoli.forum.meta;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 1/22/17.
 */

public class NightModeHelper{
    private static final String MODE = "night_mode";
    private static final String NIGHT = "night";
    private static final String DAY = "day";

    public static SharedPreferences getSp(Context context){
        return context.getSharedPreferences(MODE,MODE_PRIVATE);
    }

    public static boolean IsDay(Context context){
        String temp = getSp(context).getString(MODE,DAY);
        return temp.equals(DAY);
    }

    public static void setNight(Context context){
        SharedPreferences.Editor editor = getSp(context).edit();
        editor.putString(MODE,NIGHT);
        editor.apply();
    }

    public static void setDay(Context context){
        SharedPreferences.Editor editor = getSp(context).edit();
        editor.putString(MODE,DAY);
        editor.apply();
    }
}
