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
    private SharedPreferences sharedPreferences;
    public NightModeHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(MODE,MODE_PRIVATE);
    }

    public final boolean IsDay(){
        String temp = sharedPreferences.getString(MODE,DAY);
        return temp.equals(DAY);
    }

    public final void setNight(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MODE,NIGHT);
        editor.apply();
    }

    public final void setDay(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MODE,DAY);
        editor.apply();
    }
}
