package com.geno.chaoli.forum.binding;

import android.databinding.BindingAdapter;
import android.support.design.widget.NavigationView;

/**
 * Created by jianhao on 16-9-21.
 */

public class NavigationBindingAdapter {
    @BindingAdapter("app:menu")
    public static void setMenu(NavigationView navigationView, int menuId) {
        if (navigationView.getMenu() != null) navigationView.getMenu().clear();
        navigationView.inflateMenu(menuId);
    }
}
