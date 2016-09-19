package com.geno.chaoli.forum.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.utils.ConversationUtils;
import com.geno.chaoli.forum.meta.DividerItemDecoration;
import com.geno.chaoli.forum.meta.OnlineImgTextView;
import com.geno.chaoli.forum.meta.PostContentView;
import com.geno.chaoli.forum.model.Post;
import com.geno.chaoli.forum.model.PostListResult;
import com.geno.chaoli.forum.network.MyRetrofit;
import com.geno.chaoli.forum.utils.PostUtils;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends BaseActivity implements ConversationUtils.IgnoreAndStarConversationObserver
{
	public static final String TAG = "PostActivity";

	private static final int REPLY_CODE = 1;
	private static final int POST_NUM_PER_PAGE = 20;

	private final Context mContext = this;

	public static final int menu_settings = 0;
	public static final int menu_share = 1;
	public static final int menu_author_only = 2;
	public static final int menu_star = 3;

	@BindView(R.id.reply)
	public FloatingActionButton reply;

	public static SharedPreferences sp;
	public SharedPreferences.Editor e;

	int mConversationId;

	String mTitle;
	int mPage;

	boolean isAuthorOnly;

	@BindView(R.id.postList)
	RecyclerView postListRv;
	@BindView(R.id.swipyRefreshLayout)
	SwipyRefreshLayout swipyRefreshLayout;

	PostListAdapter mPostListAdapter;
	LinearLayoutManager mLinearLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity);

		ButterKnife.bind(this);

		Bundle data = getIntent().getExtras();
		mConversationId = data.getInt("conversationId");
		mTitle = data.getString("title", "");
		setTitle(mTitle);
		mPage = data.getInt("page", 1);
		isAuthorOnly = data.getBoolean("isAuthorOnly", false);
		sp = getSharedPreferences(Constants.postSP + mConversationId, MODE_PRIVATE);

		configToolbar(mTitle);

		swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
		swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(SwipyRefreshLayoutDirection direction) {
				loadMore();
			}
		});

		reply.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent toReply = new Intent(PostActivity.this, ReplyAction.class);
				toReply.putExtra("conversationId", mConversationId);
				startActivityForResult(toReply, REPLY_CODE);
			}
		});

		mLinearLayoutManager = new LinearLayoutManager(mContext);
		mPostListAdapter = new PostListAdapter(mContext, new ArrayList<Post>());
        postListRv.setLayoutManager(mLinearLayoutManager);
		postListRv.setAdapter(mPostListAdapter);
		postListRv.addItemDecoration(new DividerItemDecoration(mContext));


		//final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.loading_posts));

		//trigger the circle to animate
		swipyRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipyRefreshLayout.setRefreshing(true);
			}
		});
		getList(0);
	}

	/**
	 * 针对帖子读取到最后的情况，只往帖子列表中增加多出来的帖子
	 * @param A 已有的帖子列表
	 * @param B 获取到的一页帖子列表
     * @return 新帖子列表的长度
     */
	private int expandUnique(List<Post> A, List<Post> B) {
		int lenA = A.size();
		if (lenA == 0) {
			A.addAll(B);
		} else {
			int i;
			for (i = 0; i < B.size(); i++)
				if (B.get(i).getTime() > A.get(lenA - 1).getTime())
					break;
			A.addAll(B.subList(i, B.size()));
		}
		return A.size();
	}

	private void getList(final int page) {
		MyRetrofit.getService()
				.listPosts(mConversationId, page)
				.enqueue(new retrofit2.Callback<PostListResult>() {
					@Override
					public void onResponse(retrofit2.Call<PostListResult> call, retrofit2.Response<PostListResult> response) {
						List<Post> newPostList = response.body().getPosts();
						List<Post> postList = mPostListAdapter.getPosts();
						int oldLen = postList.size();
						expandUnique(postList, newPostList);
						mPostListAdapter.setPosts(postList);
						mPostListAdapter.notifyItemRangeInserted(oldLen, postList.size() - oldLen);

						//postListRv.smoothScrollToPosition(mPage * POST_NUM_PER_PAGE + 1);
						swipyRefreshLayout.setRefreshing(false);
						mPage = (postList.size() + POST_NUM_PER_PAGE - 1) / POST_NUM_PER_PAGE;
						postListRv.smoothScrollToPosition(page == 1 ? 0 : oldLen);
					}

					@Override
					public void onFailure(retrofit2.Call<PostListResult> call, Throwable t) {
						swipyRefreshLayout.setRefreshing(false);
						Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
						t.printStackTrace();
					}
				});
	}

	private void loadMore() {
		final List<Post> postList = mPostListAdapter.getPosts();
		getList(postList.size() < mPage * POST_NUM_PER_PAGE ? mPage : mPage + 1);
	}

	class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {
		List<Post> mPosts;
		Context mContext;
		public PostListAdapter(Context context, List<Post> posts) {
			mContext = context;
			mPosts = posts;
		}

		public void setPosts(List<Post> posts) {
			this.mPosts = posts;
		}
		public List<Post> getPosts() {
			return mPosts;
		}

		@Override
		public int getItemCount() {
			return mPosts.size();
		}

		@Override
		public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new PostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.post_view, parent, false));
		}

		@Override
		public void onBindViewHolder(final PostViewHolder holder, final int position) {
			final Post post = mPosts.get(position);
			holder.avatar.update(mContext, post.getAvatarFormat(), post.getMemberId(), post.getUsername());
			holder.avatar.scale(35);
			holder.usernameAndSignature.setText(post.signature == null ? post.username : getString(R.string.comma, post.username, post.signature));
			holder.floor.setText(String.format(Locale.getDefault(), "%d", post.getFloor()));

			if (post.deleteMemberId != 0)
			{
				holder.itemView.setBackgroundColor(0xFF808080);
				holder.avatar.setVisibility(View.GONE);
				//signature.setVisibility(GONE);
				holder.content.setVisibility(View.GONE);
			} else {
				holder.itemView.setBackgroundColor(Color.WHITE);
				holder.avatar.setVisibility(View.VISIBLE);
				holder.content.setVisibility(View.VISIBLE);
			}

			holder.avatar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i(TAG, "click");
					Intent intent = new Intent(mContext, HomepageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("username", holder.avatar.getUsername());
					bundle.putInt("userId", holder.avatar.getUserId());
					bundle.putString("avatarSuffix", holder.avatar.getImagePath());
					bundle.putString("signature", post.signature);
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});
			holder.content.init(mContext);
			holder.content.setPost(post);
			holder.content.setConversationId(mConversationId);
			for (int i = 0; i < holder.content.getChildCount(); i++) {
				View child = holder.content.getChildAt(i);
				if (child instanceof OnlineImgTextView) {
					child.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View view) {
							new AlertDialog.Builder(mContext).setTitle("请选择")
									.setItems(new String[]{getString(R.string.reply)}, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											switch (i){
												case 0:
													Intent toReply = new Intent(PostActivity.this, ReplyAction.class);
													toReply.putExtra("conversationId", mConversationId);
													toReply.putExtra("postId", post.getPostId());
													toReply.putExtra("replyTo", post.getUsername());
													toReply.putExtra("replyMsg", PostUtils.removeQuote(post.getContent()));
													Log.d(TAG, "onClick: content = " + post.getContent() + ", replyMsg = " + PostUtils.removeQuote(post.getContent()));
													startActivityForResult(toReply, REPLY_CODE);
													break;
											}
										}
									})
									.show();
							return true;
						}
					});
				}
			}
			for (int i = 0; i < ((LinearLayout) holder.itemView).getChildCount(); i++) {
				View child = ((LinearLayout) holder.itemView).getChildAt(i);
				if(child instanceof ImageView) {
					((LinearLayout) holder.itemView).removeViewAt(i);
				}
			}
		}

		class PostViewHolder extends RecyclerView.ViewHolder {
			@BindView(R.id.avatar)
			AvatarView avatar;
			@BindView(R.id.usernameAndSignature)
			TextView usernameAndSignature;
			@BindView(R.id.floor)
			TextView floor;
			@BindView(R.id.content)
			PostContentView content;
			PostViewHolder(View view){
				super(view);
				ButterKnife.bind(this, view);
			}
			
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REPLY_CODE) {
			if (resultCode == RESULT_OK) {
				swipyRefreshLayout.setRefreshing(true);
				loadMore();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, menu_settings, R.string.settings).setIcon(android.R.drawable.ic_menu_manage);
		menu.add(Menu.NONE, Menu.NONE, menu_share, R.string.share).setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(Menu.NONE, Menu.NONE, menu_author_only, isAuthorOnly ? R.string.cancel_author_only : R.string.author_only).setIcon(android.R.drawable.ic_menu_view);
		menu.add(Menu.NONE, Menu.NONE, menu_star, R.string.star).setIcon(R.drawable.ic_menu_star).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
										ConversationUtils.ignoreConversation(PostActivity.this, mConversationId, PostActivity.this);
										break;
									case 1:
										Toast.makeText(PostActivity.this, R.string.mark_as_unread, Toast.LENGTH_SHORT).show();
										break;
								}
							}
						});
				ab.show();
				break;
			case menu_share:
				Intent share = new Intent();
				share.setAction(Intent.ACTION_SEND);
				share.putExtra(Intent.EXTRA_TEXT, Constants.postListURL + mConversationId);
				share.setType("text/plain");
				startActivity(Intent.createChooser(share, getString(R.string.share)));
				break;
			case menu_author_only:
				finish();
				Intent author_only = new Intent(PostActivity.this, PostActivity.class);
				author_only.putExtra("conversationId", mConversationId);
				author_only.putExtra("page", isAuthorOnly ? "" : "?author=lz");
				author_only.putExtra("title", mTitle);
				author_only.putExtra("isAuthorOnly", !isAuthorOnly);
				startActivity(author_only);
				break;
			case menu_star:
				// TODO: 16-3-28 2201 Star light
				ConversationUtils.starConversation(PostActivity.this, mConversationId, PostActivity.this);
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

}
