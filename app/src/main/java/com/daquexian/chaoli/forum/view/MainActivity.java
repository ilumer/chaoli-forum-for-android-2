package com.daquexian.chaoli.forum.view;

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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.daquexian.chaoli.forum.data.Me;
import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.databinding.MainActivityBinding;
import com.daquexian.chaoli.forum.databinding.NavigationHeaderBinding;
import com.daquexian.chaoli.forum.meta.DividerItemDecoration;
import com.daquexian.chaoli.forum.model.Conversation;
import com.daquexian.chaoli.forum.meta.Constants;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;
import com.daquexian.chaoli.forum.viewmodel.MainActivityVM;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class MainActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener
{
	public static final String TAG = "MainActivity";

	public Toolbar toolbar;
	public DrawerLayout mDrawerLayout;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	private Context mContext = this;

	private ProgressDialog loginProgressDialog;

	private int POST_CONVERSATION_CODE = 1;

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	RecyclerView l;

	public SwipyRefreshLayout swipyRefreshLayout;

	ActionBarDrawerToggle actionBarDrawerToggle;

	MainActivityVM viewModel;
	MainActivityBinding binding;

	Boolean bottom = true;	//是否滚到底部

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewModel = new MainActivityVM();
		setViewModel(viewModel);

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

		l = binding.conversationList;
		final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
		l.setLayoutManager(layoutManager);
		l.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(mContext, android.support.v7.widget.DividerItemDecoration.VERTICAL));

		swipyRefreshLayout = binding.conversationListRefreshLayout;

		binding.appbar.addOnOffsetChangedListener(this);
		binding.conversationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				//得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount()-1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom =  recyclerView.getBottom()-recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastVisiblePosition  = layoutManager.findLastVisibleItemPosition();

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
				int lastPosition = recyclerView.getLayoutManager().getItemCount() - 1;
				if(lastChildBottom == recyclerBottom && lastVisiblePosition == lastPosition){
                    bottom = true;
                    viewModel.canRefresh.set(true);
                }else{
                    bottom = false;
                }
				if (lastVisiblePosition >= lastPosition - 3) viewModel.tryToLoadFromBottom();
			}
		});
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
				Uri.parse("android-app://com.daquexian.chaoli.forum/http/host/path")
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
				Uri.parse("android-app://com.daquexian.chaoli.forum/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == POST_CONVERSATION_CODE) {
			if (resultCode == RESULT_OK) {
				viewModel.refresh();
			}
		}
	}

	public void goToConversation(Conversation conversation) {
		Intent jmp = new Intent();
		jmp.putExtra("conversation", conversation);
		//jmp.putExtra("conversationId", conversation.getConversationId());
		//jmp.putExtra("conversationTitle", conversation.getTitle());
		jmp.setClass(this, PostActivity.class);
		startActivity(jmp);
	}

	public void goToMyHomePage() {
		Log.d(TAG, "goToMyHomePage: " + Me.isEmpty());
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
		startActivityForResult(intent, POST_CONVERSATION_CODE);
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
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		/**
		 * verticalOffset == 0说明appbar已经是展开状态
		 */
		viewModel.canRefresh.set(verticalOffset == 0 || bottom);
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
