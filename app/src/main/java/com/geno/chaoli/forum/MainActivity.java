package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.FragmentManager;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.AccountUtils;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
	public static final String TAG = "MainActivity";

	public Toolbar toolbar;
	public NavigationDrawerFragment fragment;

	public CharSequence title;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public boolean loggedIn = false;

	private Boolean delayShowConversations;

	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		//if (getActionBar() != null)
			//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.avatar_32));

		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			delayShowConversations = bundle.getBoolean("delayShowConversations", false);
		}

		toolbar = (Toolbar) findViewById(R.id.tl_custom);
		toolbar.setTitle(R.string.app_name);
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

		FloatingActionButton postBtn = (FloatingActionButton) findViewById(R.id.postBtn);
		postBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PostAction.class);
				startActivity(intent);
			}
		});

		fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.drawer);
		title = getTitle();

		fragment.setUp(R.id.drawer, (DrawerLayout) findViewById(R.id.drawer_main), toolbar);


		final ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.logging_in));
		LoginUtils.begin_login(this, new LoginUtils.LoginObserver()
		{
			@Override
			public void onLoginSuccess(int userId, String token)
			{
				progressDialog.dismiss();
				loggedIn = true;
				((TextView) fragment.getActivity().findViewById(R.id.loginHWndUsername)).setText(getSharedPreferences("username_and_password", MODE_PRIVATE).getString("username", ""));
				//((AvatarView) fragment.getActivity().findViewById(R.id.avatar)).setAvatarView(new AvatarView(MainActivity.this, "png", userId, getSharedPreferences("username_and_password", MODE_PRIVATE).getString("username", "")));
				ListView channelSelect = (ListView) fragment.getActivity().findViewById(R.id.channelSelect);
				channelSelect.setAdapter(new ChannelAdapter(true));
				fragment.selectItem(0);
			}

			@Override
			public void onLoginFailure(int statusCode)
			{
				progressDialog.dismiss();
				ListView channelSelect = (ListView) fragment.getActivity().findViewById(R.id.channelSelect);
				channelSelect.setAdapter(new ChannelAdapter(false));
				fragment.selectItem(0);
				Log.d(TAG, "onLoginFailure: " + statusCode);
				/*if (statusCode == 5)
				{
					Toast.makeText(MainActivity.this, "Cookie expired, please login again.", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(MainActivity.this, LoginActivity.class));
				}*/
			}
		});
	}

	@Override
	public void onNavigationDrawerItemSelected(int position)
	{
		if(delayShowConversations == null || delayShowConversations){
			delayShowConversations = false;
		}else {
			FragmentManager fm = getFragmentManager();
			ConversationListFragment c = new ConversationListFragment().setChannel(getChannel(position, loggedIn));
			fm.beginTransaction().replace(R.id.main_view, c).commit();
		}
	}

	public String getChannel(int position, boolean loggedIn)
	{
		String[] channel =
		new String[]
				{
						"",
						loggedIn ? Channel.caff.toString() : null,
						Channel.maths.toString(),
						Channel.physics.toString(),
						Channel.chem.toString(),
						Channel.biology.toString(),
						Channel.tech.toString(),
						Channel.court.toString(),
						Channel.announ.toString(),
						Channel.others.toString(),
						Channel.socsci.toString(),
						Channel.lang.toString(),
				};
		return channel[position];
	}

	class ChannelAdapter extends BaseAdapter {
		ChannelAdapter(Boolean loggedIn){
			if(loggedIn){
				channels = new String[]
						{
								getString(R.string.channel_all),
								getString(R.string.channel_caff),
								getString(R.string.channel_maths),
								getString(R.string.channel_physics),
								getString(R.string.channel_chem),
								getString(R.string.channel_biology),
								getString(R.string.channel_tech),
								getString(R.string.channel_court),
								getString(R.string.channel_announ),
								getString(R.string.channel_others),
								getString(R.string.channel_socsci),
								getString(R.string.channel_lang),
						};
			}else{
				channels = new String[]
						{
								getString(R.string.channel_all),
								getString(R.string.channel_maths),
								getString(R.string.channel_physics),
								getString(R.string.channel_chem),
								getString(R.string.channel_biology),
								getString(R.string.channel_tech),
								getString(R.string.channel_court),
								getString(R.string.channel_announ),
								getString(R.string.channel_others),
								getString(R.string.channel_socsci),
								getString(R.string.channel_lang),
						};
			}
		}
		String[] channels = new String[]
				{
						getString(R.string.channel_maths),
						getString(R.string.channel_physics),
						getString(R.string.channel_chem),
						getString(R.string.channel_biology),
						getString(R.string.channel_tech),
						getString(R.string.channel_court),
						getString(R.string.channel_announ),
						getString(R.string.channel_others),
						getString(R.string.channel_socsci),
						getString(R.string.channel_lang),
				};
		@Override
		public int getCount() {
			return channels.length;
		}

		@Override
		public Object getItem(int position) {
			return channels[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.channel_item, null);
			}
			TextView channel_tv = (TextView) convertView.findViewById(R.id.channel);
			channel_tv.setText(channels[position]);
            /*if(position == mCurrentSelectedPosition){
                convertView.setBackgroundColor(getResources().getColor(R.color.black));
                channel_tv.setTextColor(getResources().getColor(R.color.white));
            }else{
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
                channel_tv.setTextColor(getResources().getColor(R.color.black));
            }*/
			return convertView;
		}
	}
}
