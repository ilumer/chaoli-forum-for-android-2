package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geno.chaoli.forum.utils.AccountUtils;
import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.utils.LoginUtils;
import com.geno.chaoli.forum.model.NotificationList;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity// implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
	public static final String TAG = "MainActivity";

	public Toolbar toolbar;
	//public NavigationDrawerFragment fragment;
	public ConversationListFragment mConversationListFragment;
	public DrawerLayout mDrawerLayout;

	public CharSequence title;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public boolean loggedIn = false;

	//private Boolean delayShowConversations;

	private Context mContext = this;

	Timer timer;
	TimerTask task;
	Handler notificationHanlder;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		//if (getActionBar() != null)
		//getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.avatar_32));

		/*Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			delayShowConversations = bundle.getBoolean("delayShowConversations", false);
		}*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.tl_custom);
		configToolbar(R.string.app_name);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
		actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
		actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
		actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		});
		actionBarDrawerToggle.syncState();
		mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

		notificationHanlder = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(!Me.isEmpty()) {
					AccountUtils.checkNotification(mContext, new AccountUtils.MessageObserver() {
						@Override
						public void onGetUpdateSuccess(Boolean hasUpdate) {

						}

						@Override
						public void onGetUpdateFailure(int statusCode) {

						}

						@Override
						public void onCheckNotificationSuccess(NotificationList notificationList) {
							if (notificationList.count > 0){
								actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_with_a_circle_24dp);
								actionBarDrawerToggle.syncState();
							}else{
								actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
								actionBarDrawerToggle.syncState();
							}
						}

						@Override
						public void onCheckNotificationFailure(int statusCode) {

						}
					});
				}
			}
		};
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				notificationHanlder.sendEmptyMessage(0);
			}
		};
		FloatingActionButton postBtn = (FloatingActionButton) findViewById(R.id.postBtn);
		if (postBtn == null)
			throw new NullPointerException();
		postBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PostAction.class);
				startActivity(intent);
				//startActivityForResult(intent, 1);
			}
		});

		title = getTitle();

		final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
		if (navigationView == null)
			throw new NullPointerException();
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				Log.d(TAG, String.valueOf(item.getOrder()));
				selectItem(item.getOrder());
				item.setChecked(true);
				return true;
			}
		});
		final AvatarView avatar = (AvatarView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
		final TextView signatureTxt = (TextView) navigationView.getHeaderView(0).findViewById(R.id.signature_txt);

		avatar.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(loggedIn){
					if(!Me.isEmpty()) {
						Intent intent = new Intent(mContext, HomepageActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("username", Me.getUsername());
						bundle.putInt("userId", Me.getUserId());
						bundle.putString("signature", Me.getPreferences().getSignature());
						bundle.putString("avatarSuffix", Me.getAvatarSuffix() == null ? Constants.NONE : Me.getAvatarSuffix());
						bundle.putBoolean("isSelf", true);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}else {
					startActivity(new Intent(mContext, LoginActivity.class));
				}
			}
		});

		final ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.logging_in));
		LoginUtils.begin_login(this, new LoginUtils.LoginObserver() {
			@Override
			public void onLoginSuccess(int userId, String token) {
				progressDialog.dismiss();
				loggedIn = true;
				navigationView.getMenu().clear();
				navigationView.inflateMenu(R.menu.menu_navigation);
				String username = getSharedPreferences("username_and_password", MODE_PRIVATE).getString("username", "");
				((TextView) ((Activity) mContext).findViewById(R.id.loginHWndUsername)).setText(username);
				Me.setInstanceFromSharedPreference(mContext, username);
				if (!Me.isEmpty()) {
					//Log.d(TAG, "AvatarSuffix: " + User.getMyAvatarSuffix());
					avatar.update(mContext, Me.getMyAvatarSuffix(), Me.getMyUserId(), Me.getMyUsername());
					signatureTxt.setText(Me.getMySignature());
				}
				AccountUtils.getProfile(mContext, new AccountUtils.GetProfileObserver() {
					@Override
					public void onGetProfileSuccess() {
						//Log.d(TAG, "AvatarSuffix: " + User.getMyAvatarSuffix());
						avatar.update(mContext, Me.getMyAvatarSuffix(), Me.getMyUserId(), Me.getMyUsername());
						signatureTxt.setText(Me.getMySignature());
					}

					@Override
					public void onGetProfileFailure() {

					}
				});

				try {
					timer.schedule(task, Constants.getNotificationInterval * 1000, Constants.getNotificationInterval * 1000);
				} catch (Exception e) {

				}
				selectItem(0);
			}

			@Override
			public void onLoginFailure(int statusCode) {
				progressDialog.dismiss();
				navigationView.getMenu().clear();
				navigationView.inflateMenu(R.menu.menu_navigation_no_login);
				avatar.setLoginImage(mContext);
				selectItem(0);
				Log.d(TAG, "onLoginFailure: " + statusCode);
			}
		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	public void selectItem(int position) {
		FragmentManager fm = getFragmentManager();
		ConversationListFragment c = new ConversationListFragment().setChannel(getChannel(position, loggedIn));
		mConversationListFragment = c;
		fm.beginTransaction().replace(R.id.main_view, c).commit();
		mDrawerLayout.closeDrawers();
	}

	public String getChannel(int position, boolean loggedIn) {
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


	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "resume");
		if(!Me.isEmpty()) {
			//timer.cancel();
			task.cancel();
			task = new TimerTask() {
				@Override
				public void run() {
					notificationHanlder.sendEmptyMessage(0);
				}
			};
			//timer = new Timer();
			timer.schedule(task, 0, Constants.getNotificationInterval * 1000);
		}
	}

	class ChannelAdapter extends BaseAdapter {
		ChannelAdapter(Boolean loggedIn) {
			if (loggedIn) {
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
			} else {
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
			if (convertView == null) {
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


	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a mTitle for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.geno.chaoli.forum/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a mTitle for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.geno.chaoli.forum/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}
