package com.geno.chaoli.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.LoginUtils;

/**
 * Created by daquexian on 16-4-14.
 */

public class HomepageActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener{
    String mUsername, mSignature, mAvatarSuffix;
    int mUserId;
    Boolean isSelf;


    final String TAG = "HomePageActivity";
    final Context mContext = this;

    final int MENU_SETTING = 0;
    final int MENU_LOGOUT = 1;

    AppBarLayout mAppBarLayout;
    ViewPager mViewPager;
    TabLayout mTabLayout;

    HistoryFragment mHistoryFragment;
    StatisticFragment mStastisticFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            Log.e(TAG, "bundle can't be null");
            this.finish();
            return;
        }

        mUsername = bundle.getString("username", "");
        mSignature = bundle.getString("signature", "");
        mUserId = bundle.getInt("userId", -1);
        //avatarURL = bundle.getString("avatarURL", "");
        mAvatarSuffix = bundle.getString("avatarSuffix", Constants.NONE);
        isSelf = bundle.getBoolean("isSelf", false);
        if("".equals(mUsername) || mUserId == -1){
            this.finish();
            return;
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mUsername);
        collapsingToolbarLayout.setExpandedTitleGravity(0x01 | 0x50);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tl_custom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //final SwipyRefreshLayout mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.mSwipyRefreshLayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        AvatarView avatarView = (AvatarView) findViewById(R.id.ivAvatar);
        avatarView.update(this, mAvatarSuffix, mUserId, mUsername);
        TextView tvSignature = (TextView) findViewById(R.id.tvSignature);
        tvSignature.setText(mSignature);

        mViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.activity));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.statistics));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
        mViewPager.addOnPageChangeListener(listener);
        //mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        MyViewPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    mHistoryFragment = new HistoryFragment();
                    Bundle args0 = new Bundle();
                    args0.putInt("userId", mUserId);
                    args0.putString("username", mUsername);
                    args0.putString("avatarSuffix", mAvatarSuffix);
                    mHistoryFragment.setArguments(args0);
                    Log.d(TAG, "Hi!!!");
                    return mHistoryFragment;
                case 1:
                    mStastisticFragment = new StatisticFragment();
                    Bundle args1 = new Bundle();
                    args1.putInt("userId", mUserId);
                    mStastisticFragment.setArguments(args1);
                    return mStastisticFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(isSelf) {
            menu.add(Menu.NONE, Menu.NONE, MENU_SETTING, R.string.config).setIcon(R.drawable.ic_account_settings_variant).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add(Menu.NONE, Menu.NONE, MENU_LOGOUT, R.string.logout).setIcon(R.drawable.ic_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getOrder()){
            case MENU_SETTING:
                startActivity(new Intent(HomepageActivity.this, SettingsActivity.class));
                break;
            case MENU_LOGOUT:
                LoginUtils.logout(this, new LoginUtils.LogoutObserver() {
                    @Override
                    public void onLogoutSuccess() {
                    }

                    @Override
                    public void onLogoutFailure(int statusCode) {
                    }
                });
                // whether logout succeeds or not, the data in app will be wiped, so we can think it always succeed
                Toast.makeText(getApplicationContext(), R.string.logout_success, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomepageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        Log.d(TAG, String.valueOf(i));
        //The Refresh must be only active when the offset is zero :
        mHistoryFragment.setRefreshEnabled(i == 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }
}