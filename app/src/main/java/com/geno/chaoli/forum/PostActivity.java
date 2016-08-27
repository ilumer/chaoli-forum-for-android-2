package com.geno.chaoli.forum;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
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

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationUtils;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LaTeXtView;
import com.geno.chaoli.forum.meta.PostContentView;
import com.geno.chaoli.forum.model.Post;
import com.geno.chaoli.forum.model.PostListResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class PostActivity extends BaseActivity implements ConversationUtils.IgnoreAndStarConversationObserver
{
	public static final String TAG = "PostActivity";

	private final Context mContext = this;

	public static final int menu_settings = 0;
	public static final int menu_share = 1;
	public static final int menu_author_only = 2;
	public static final int menu_star = 3;

	@BindView(R.id.reply)
	public FloatingActionButton reply;

	public static SharedPreferences sp;
	public SharedPreferences.Editor e;

	public int conversationId;

	public String title, intentToPage;

	public boolean isAuthorOnly;

	public static AsyncHttpClient client = new AsyncHttpClient();

	@BindView(R.id.postList)
	RecyclerView postList;

	PostListAdapter mPostListAdapter;
	LinearLayoutManager mLinearLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity);

		ButterKnife.bind(this);

		Bundle data = getIntent().getExtras();
		conversationId = data.getInt("conversationId");
		title = data.getString("title", "");
		setTitle(title);
		intentToPage = data.getString("intentToPage", "");
		isAuthorOnly = data.getBoolean("isAuthorOnly", false);
		sp = getSharedPreferences(Constants.postSP + conversationId, MODE_PRIVATE);

		configToolbar(title);

		reply.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent toReply = new Intent(PostActivity.this, ReplyAction.class);
				toReply.putExtra("conversationId", conversationId);
				startActivity(toReply);
			}
		});

		mLinearLayoutManager = new LinearLayoutManager(mContext);
		mPostListAdapter = new PostListAdapter(mContext, new ArrayList<Post>());
        postList.setLayoutManager(mLinearLayoutManager);
		postList.setAdapter(mPostListAdapter);

		CookieUtils.saveCookie(client, this);
		final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.loading_posts));
		client.get(this, Constants.postListURL + conversationId + intentToPage, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				String response = new String(responseBody);
				PostListResult result = JSON.parseObject(response, PostListResult.class);
				List<Post> posts = result.getPosts();
				mPostListAdapter.setPosts(posts);
				mPostListAdapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				progressDialog.dismiss();
				Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
			}
		});
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

		@Override
		public int getItemCount() {
			return mPosts.size();
		}

		@Override
		public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new PostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.post_view, parent, false));
		}

		@Override
		public void onBindViewHolder(final PostViewHolder holder, int position) {
			final Post post = mPosts.get(position);
			holder.avatar.update(mContext, post.getAvatarFormat(), post.getMemberId(), post.getUsername());
			holder.avatar.scale(35);
			holder.usernameAndSignature.setText(post.username + (post.signature == null ? "" : (", " + post.signature)));
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
			holder.content.setText(post.getContent());
			//holder.content.setText(mContext, post.getContent());
			for (int i = 0; i < ((LinearLayout) holder.itemView).getChildCount(); i++) {
				View child = ((LinearLayout) holder.itemView).getChildAt(i);
				if(child instanceof ImageView) {
					((LinearLayout) holder.itemView).removeViewAt(i);
				}
			}
			if (post.getAttachments() != null && post.getAttachments().size() > 0) {
				for (Post.Attachment attachment : post.getAttachments()) {
					if (attachment.getFileName().endsWith(".jpg") || attachment.getFileName().endsWith(".png")) {
						ImageView imageView = new ImageView(mContext);
						Glide.with(mContext)
								.load(Constants.ATTACHMENT_IMAGE_URL + attachment.getAttachmentId() + attachment.getSecret())
								.into(imageView);
						((LinearLayout)holder.itemView).addView(imageView);
					}
				}
			}
			//holder.content.setMovementMethod(LinkMovementMethod.getInstance());
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
										ConversationUtils.ignoreConversation(PostActivity.this, conversationId, PostActivity.this);
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
				share.putExtra(Intent.EXTRA_TEXT, Constants.postListURL + conversationId);
				share.setType("text/plain");
				startActivity(Intent.createChooser(share, getString(R.string.share)));
				break;
			case menu_author_only:
				finish();
				Intent author_only = new Intent(PostActivity.this, PostActivity.class);
				author_only.putExtra("conversationId", conversationId);
				author_only.putExtra("intentToPage", isAuthorOnly ? "" : "?author=lz");
				author_only.putExtra("title", title);
				author_only.putExtra("isAuthorOnly", !isAuthorOnly);
				startActivity(author_only);
				break;
			case menu_star:
				// TODO: 16-3-28 2201 Star light
				ConversationUtils.starConversation(PostActivity.this, conversationId, PostActivity.this);
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
