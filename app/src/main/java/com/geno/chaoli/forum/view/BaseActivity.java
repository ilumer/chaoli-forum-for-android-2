package com.geno.chaoli.forum.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.geno.chaoli.forum.R;

/**
 * Created by jianhao on 16-5-31.
 */
public class BaseActivity extends AppCompatActivity {
    public void configToolbar(String title){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void configToolbar(int resId){
        configToolbar(getString(resId));
    }
}
