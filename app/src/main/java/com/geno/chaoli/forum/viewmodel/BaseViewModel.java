package com.geno.chaoli.forum.viewmodel;

import android.content.SharedPreferences;

import com.geno.chaoli.forum.ChaoliApplication;
import com.geno.chaoli.forum.view.IView;

/**
 * Created by jianhao on 16-9-20.
 */

public abstract class BaseViewModel {
    protected String getString(int resId) {
        return ChaoliApplication.getAppContext().getString(resId);
    }

    protected SharedPreferences getSharedPreferences(String name, int mode) {
        return ChaoliApplication.getAppContext().getSharedPreferences(name, mode);
    }
}
