package com.geno.chaoli.forum.binding;

import android.databinding.BindingAdapter;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

/**
 * SwipyRefreshLayout的BindingAdapter
 * Created by daquexian on 16-9-19.
 */
public class SwipyRefreshAdapter {
    //trigger the circle to animate
    @BindingAdapter("app:isRefreshing")
    public static void setRefreshing(final SwipyRefreshLayout swipyRefreshLayout, final Boolean isRefreshing) {
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }
}
