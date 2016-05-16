package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
	public static final String TAG = "MainActivity";

	public NavigationDrawerFragment fragment;

	public CharSequence title;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public boolean loggedIn = false;

	private Boolean delayShowConversations;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			delayShowConversations = bundle.getBoolean("delayShowConversations", false);
		}

		fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.drawer);
		title = getTitle();

		fragment.setUp(R.id.drawer, (DrawerLayout) findViewById(R.id.drawer_main));
		final ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.logging_in));
		LoginUtils.begin_login(this, new LoginUtils.LoginObserver()
		{
			@Override
			public void onLoginSuccess(int userId, String token)
			{
				progressDialog.dismiss();
				loggedIn = true;
				onNavigationDrawerItemSelected(0);
				((TextView) fragment.getActivity().findViewById(R.id.loginHWndUsername)).setText(getSharedPreferences("username_and_password", MODE_PRIVATE).getString("username", ""));
				//((AvatarView) fragment.getActivity().findViewById(R.id.avatar)).setAvatarView(new AvatarView(MainActivity.this, "png", userId, getSharedPreferences("username_and_password", MODE_PRIVATE).getString("username", "")));
				ListView channelSelect = (ListView) fragment.getActivity().findViewById(R.id.channelSelect);
				channelSelect.setAdapter(new ArrayAdapter<>(
						MainActivity.this,
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						new String[]
								{
										getString(R.string.channel_all),
										loggedIn ? getString(R.string.channel_caff) : null,
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
								}
				));
			}

			@Override
			public void onLoginFailure(int statusCode)
			{
				progressDialog.dismiss();
				onNavigationDrawerItemSelected(0);
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
}
