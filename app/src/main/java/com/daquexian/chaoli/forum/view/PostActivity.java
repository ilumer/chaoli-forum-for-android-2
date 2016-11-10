package com.daquexian.chaoli.forum.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.databinding.PostActivityBinding;
import com.daquexian.chaoli.forum.meta.Constants;
import com.daquexian.chaoli.forum.model.Conversation;
import com.daquexian.chaoli.forum.model.Post;
import com.daquexian.chaoli.forum.utils.ConversationUtils;
import com.daquexian.chaoli.forum.meta.DividerItemDecoration;
import com.daquexian.chaoli.forum.utils.PostUtils;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;
import com.daquexian.chaoli.forum.viewmodel.PostActivityVM;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class PostActivity extends BaseActivity implements ConversationUtils.IgnoreAndStarConversationObserver, AppBarLayout.OnOffsetChangedListener
{
	public static final String TAG = "PostActivity";

	private static final int POST_NUM_PER_PAGE = 20;
	public static final int REPLY_CODE = 1;

	private final Context mContext = this;

	public FloatingActionButton reply;

	public static SharedPreferences sp;
	public SharedPreferences.Editor e;

	Conversation mConversation;
	int mConversationId;
	String mTitle;
	int mPage;

	RecyclerView postListRv;
	SwipyRefreshLayout swipyRefreshLayout;

	LinearLayoutManager mLinearLayoutManager;

	PostActivityVM viewModel;
	PostActivityBinding binding;

	Boolean bottom = true; //是否滚动到底部

	public static final int menu_settings = 0;
	public static final int menu_reverse = menu_settings + 1;
	public static final int menu_share = menu_reverse + 1;
	public static final int menu_author_only = menu_share + 1;
	public static final int menu_star = menu_author_only + 1;

	private void initUI() {
		reply = binding.reply;
		postListRv = binding.postList;
		swipyRefreshLayout = binding.swipyRefreshLayout;
		binding.appbar.addOnOffsetChangedListener(this);
		binding.postList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				//得到当前显示的最后一个item的view
				View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount()-1);
				if (lastChildView == null) return;
				//得到lastChildView的bottom坐标值
				int lastChildBottom = lastChildView.getBottom();
				//得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
				int recyclerBottom =  recyclerView.getBottom()-recyclerView.getPaddingBottom();
				//通过这个lastChildView得到这个view当前的position值
				int lastVisiblePosition  = recyclerView.getLayoutManager().getPosition(lastChildView);

				//判断lastChildView的bottom值跟recyclerBottom
				//判断lastPosition是不是最后一个position
				//如果两个条件都满足则说明是真正的滑动到了底部
				/* Why <= ? */
				int lastPosition = recyclerView.getLayoutManager().getItemCount() - 1;
				if(lastChildBottom <= recyclerBottom && lastVisiblePosition == lastPosition){
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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle data = getIntent().getExtras();
		mConversation = data.getParcelable("conversation");
		if (mConversation != null) {
			mConversationId = mConversation.getConversationId();
			//viewModel.setConversationId(mConversationId);
			mTitle = mConversation.getTitle();
			viewModel = new PostActivityVM(mConversation);
		} else {
			mConversationId = data.getInt("conversationId");
			mTitle = data.getString("conversationTitle");
			viewModel = new PostActivityVM(mConversationId, mTitle);
		}
		setTitle(mTitle);
		//viewModel.setTitle(mTitle);
		mPage = data.getInt("page", 1);
		setViewModel(viewModel);
		viewModel.setPage(mPage);
		viewModel.setAuthorOnly(data.getBoolean("isAuthorOnly", false));
		sp = getSharedPreferences(Constants.postSP + mConversationId, MODE_PRIVATE);

		initUI();

		configToolbar(mTitle);

		swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
		swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(SwipyRefreshLayoutDirection direction) {
				if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    PostActivity.this.viewModel.pullFromBottom();
				} else {
					PostActivity.this.viewModel.pullFromTop();
				}
			}
		});

		mLinearLayoutManager = new LinearLayoutManager(mContext);
        postListRv.setLayoutManager(mLinearLayoutManager);
		postListRv.addItemDecoration(new DividerItemDecoration(mContext));

		viewModel.firstLoad();

		this.viewModel.goToReply.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				Intent toReply = new Intent(mContext, ReplyAction.class);
				toReply.putExtra("conversationId", PostActivity.this.viewModel.conversationId);
				startActivityForResult(toReply, REPLY_CODE);
			}
		});

		this.viewModel.showToast.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showToast(PostActivity.this.viewModel.toastContent.get());
			}
		});

		this.viewModel.goToHomepage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				Post post = PostActivity.this.viewModel.clickedPost;
				Intent intent = new Intent(PostActivity.this, HomepageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("username", post.getUsername());
				bundle.putInt("userId", post.getMemberId());
				bundle.putString("signature", post.getSignature());
				bundle.putString("avatarSuffix", post.getAvatarFormat() == null ? Constants.NONE : post.getAvatarFormat());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		this.viewModel.goToQuote.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				quote(PostActivity.this.viewModel.clickedPost);
			}
		});
	}

	private void quote(Post post) {
		Intent toReply = new Intent(PostActivity.this, ReplyAction.class);
		toReply.putExtra("conversationId", mConversationId);
		toReply.putExtra("postId", post.getPostId());
		toReply.putExtra("replyTo", post.getUsername());
		toReply.putExtra("replyMsg", PostUtils.removeQuote(post.getContent()));
		startActivityForResult(toReply, REPLY_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REPLY_CODE) {
			if (resultCode == RESULT_OK) {
				viewModel.replyComplete();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, menu_reverse, R.string.descend).setIcon(R.drawable.ic_sort_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, Menu.NONE, menu_share, R.string.share).setIcon(R.drawable.ic_share_black_24dp);//.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(Menu.NONE, Menu.NONE, menu_star, R.string.star).setIcon(R.drawable.ic_menu_star);
		menu.add(Menu.NONE, Menu.NONE, menu_settings, R.string.settings).setIcon(android.R.drawable.ic_menu_manage);
		menu.add(Menu.NONE, Menu.NONE, menu_author_only, viewModel.isAuthorOnly() ? R.string.cancel_author_only : R.string.author_only).setIcon(android.R.drawable.ic_menu_view);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getOrder())
		{
			case menu_settings:
				CharSequence[] settingsMenu = {getString(R.string.ignore_this), getString(R.string.mark_as_unread)};
				AlertDialog.Builder ab = new AlertDialog.Builder(this)
						.setTitle(R.string.settings)
						.setCancelable(true)
						.setItems(settingsMenu, new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								switch (which)
								{
									case 0:
										ConversationUtils.ignoreConversation(mContext, viewModel.conversationId, (PostActivity) mContext);
										break;
									case 1:
										showToast(R.string.mark_as_unread);
										break;
								}
							}
						});
				ab.show();
				break;
			case menu_reverse:
				if (viewModel.isReversed()) {
					item.setTitle(R.string.descend);
				} else {
					item.setTitle(R.string.ascend);
				}
				viewModel.reverse();
				break;
			case menu_share:
				share();
				break;
			case menu_author_only:
				/*finish();
				Intent author_only = new Intent(this, PostActivity.class);
				author_only.putExtra("conversationId", viewModel.conversationId);
				author_only.putExtra("page", viewModel.isAuthorOnly ? "" : "?author=lz");
				author_only.putExtra("title", viewModel.title);
				author_only.putExtra("isAuthorOnly", !isAuthorOnly);
				startActivity(author_only);*/
				break;
			case menu_star:
				// TODO: 16-3-28 2201 Star light
				ConversationUtils.starConversation(this, viewModel.conversationId, this);
				break;
		}

		return true;
	}

	@Override
	public void onIgnoreConversationSuccess(Boolean isIgnored)
	{
		Toast.makeText(PostActivity.this, isIgnored ? R.string.ignore_this_success : R.string.ignore_this_cancel_success, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onIgnoreConversationFailure(int statusCode)
	{
		Toast.makeText(PostActivity.this, getString(R.string.failed, statusCode), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStarConversationSuccess(Boolean isStarred)
	{
		Toast.makeText(PostActivity.this, isStarred ? R.string.star_success : R.string.star_cancel_success, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStarConversationFailure(int statusCode)
	{
		Toast.makeText(PostActivity.this, getString(R.string.failed, statusCode), Toast.LENGTH_SHORT).show();
	}

	private void share() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.postListURL + viewModel.conversationId);
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
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
		this.viewModel = (PostActivityVM) viewModel;
		binding = DataBindingUtil.setContentView(this, R.layout.post_activity);
		binding.setViewModel(this.viewModel);
	}
}
