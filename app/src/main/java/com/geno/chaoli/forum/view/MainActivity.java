package com.geno.chaoli.forum.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.geno.chaoli.forum.data.Me;
import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.databinding.MainActivityBinding;
import com.geno.chaoli.forum.databinding.NavigationHeaderBinding;
import com.geno.chaoli.forum.meta.DividerItemDecoration;
import com.geno.chaoli.forum.model.Conversation;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.viewmodel.BaseViewModel;
import com.geno.chaoli.forum.viewmodel.MainActivityVM;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
{
	public static final String TAG = "MainActivity";

	public Toolbar toolbar;
	public DrawerLayout mDrawerLayout;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	private Context mContext = this;

	private ProgressDialog loginProgressDialog;

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@BindView(R.id.conversationList)
	RecyclerView l;

	@BindView(R.id.conversationListRefreshLayout)
	public SwipyRefreshLayout swipyRefreshLayout;

	ActionBarDrawerToggle actionBarDrawerToggle;

	MainActivityVM viewModel;
	MainActivityBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewModel = new MainActivityVM();
		setViewModel(viewModel);
		ButterKnife.bind(this);

		initUI();


		final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem item) {
				selectItem(item.getOrder());
				item.setChecked(true);
				return true;
			}
		});

		viewModel.goToLogin.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				goToLogin();
			}
		});

		viewModel.goToHomepage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				goToMyHomePage();
			}
		});

		viewModel.goToConversation.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				goToConversation(viewModel.clickedConversation);
			}
		});

		/*viewModel.listPosition.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				smoothScrollToPosition(((ObservableInt) observable).get());
			}
		});*/

		viewModel.notificationsNum.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				if (((ObservableInt) observable).get() > 0) {
					setCircleIndicator();
				} else {
					setNormalIndicator();
				}
			}
		});

		viewModel.showLoginProcessDialog.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				if (((ObservableBoolean) observable).get()) {
					showLoginProcessDialog();
				} else {
					dismissLoginProcessDialog();
				}
			}
		});

		viewModel.selectedItem.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				selectItem(((ObservableInt) observable).get());
			}
		});

		viewModel.goToPost.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				goToPostAction();
			}
		});

		/**
		 * 根据登录状态更改侧栏菜单
		 */
		viewModel.hasLoggedIn.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				binding.navigationView.getMenu().clear();
				binding.navigationView.inflateMenu(viewModel.hasLoggedIn.get() ? R.menu.menu_navigation : R.menu.menu_navigation_no_login);
			}
		});

		swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
		viewModel.setChannel("all");
		viewModel.login();

		swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh(SwipyRefreshLayoutDirection direction)
			{
				if (direction == SwipyRefreshLayoutDirection.TOP) {
					viewModel.refresh();
				} else {
					viewModel.loadMore();
				}
			}
		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	public void selectItem(int position) {
		viewModel.setChannel(viewModel.getChannelByPosition(position));
		viewModel.refresh();
		mDrawerLayout.closeDrawers();
	}

	public void initUI() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.tl_custom);
		configToolbar(R.string.app_name);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
		actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
		actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
		actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
		});
		actionBarDrawerToggle.syncState();
		mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

		l.setLayoutManager(new LinearLayoutManager(mContext));
		l.addItemDecoration(new DividerItemDecoration(mContext));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		viewModel.destory();
	}

	@Override
	protected void onResume() {
		super.onResume();
		viewModel.resume();
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

	public void goToConversation(Conversation conversation) {
		Intent jmp = new Intent();
		jmp.putExtra("conversationId", conversation.getConversationId());
		jmp.putExtra("conversationTitle", conversation.getTitle());
		jmp.setClass(this, PostActivity.class);
		startActivity(jmp);
	}

	public void goToMyHomePage() {
		if(viewModel.hasLoggedIn.get()){
			if(!Me.isEmpty()) {
				Intent intent = new Intent(this, HomepageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("username", Me.getUsername());
				bundle.putInt("userId", Me.getUserId());
				bundle.putString("signature", Me.getPreferences().getSignature());
				bundle.putString("avatarSuffix", Me.getAvatarSuffix() == null ? Constants.NONE : Me.getAvatarSuffix());
				bundle.putBoolean("isSelf",  true);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}

	public void goToLogin() {
		startActivity(new Intent(this, LoginActivity.class));
	}

	public void goToPostAction() {
		Intent intent = new Intent(this, PostAction.class);
		startActivity(intent);
	}

	public void setCircleIndicator() {
		actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_with_a_circle_24dp);
		actionBarDrawerToggle.syncState();
	}

	public void setNormalIndicator() {
		actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
		actionBarDrawerToggle.syncState();
	}

	public void showLoginProcessDialog() {
		loginProgressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.logging_in));
	}

	public void dismissLoginProcessDialog() {
		loginProgressDialog.dismiss();
	}

	public void smoothScrollToPosition(int pos) {
		l.smoothScrollToPosition(pos);
	}

	@Override
	public void setViewModel(BaseViewModel viewModel) {
		this.viewModel = (MainActivityVM) viewModel;
		binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
		binding.setViewModel(this.viewModel);
		NavigationHeaderBinding navigationHeaderBinding = NavigationHeaderBinding.bind(binding.navigationView.getHeaderView(0));
		navigationHeaderBinding.setViewModel(this.viewModel);
	}
}
