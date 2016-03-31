package com.geno.chaoli.forum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationUtils;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;
import com.geno.chaoli.forum.meta.Post;
import com.geno.chaoli.forum.meta.PostView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PostActivity extends AppCompatActivity implements ConversationUtils.IgnoreAndStarConversationObserver
{
	public static final String TAG = "PostActivity";

	public static final int menu_settings = 0;
	public static final int menu_share = 1;
	public static final int menu_author_only = 2;
	public static final int menu_star = 3;

	public FloatingActionButton reply;

	public static ListView postList;

	public static SharedPreferences sp;
	public SharedPreferences.Editor e;

	public int conversationId;

	public String title, intentToPage;

	public boolean isAuthorOnly;

	public static AsyncHttpClient client = new AsyncHttpClient();

	public PostView[] v;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity);
//		setSupportActionBar((Toolbar) findViewById(R.id.titleBar));
		postList = (ListView) findViewById(R.id.postList);
		Bundle data = getIntent().getExtras();
		conversationId = data.getInt("conversationId");
		title = data.getString("title", "");
		setTitle(title);
		intentToPage = data.getString("intentToPage", "");
		isAuthorOnly = data.getBoolean("isAuthorOnly", false);
		sp = getSharedPreferences(Constants.postSP + conversationId, MODE_PRIVATE);
//		e = sp.edit();
		reply = (FloatingActionButton) findViewById(R.id.reply);
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
		CookieUtils.saveCookie(client, this);
		client.get(this, Constants.postListURL + conversationId + intentToPage, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				JSONObject o = JSON.parseObject(new String(responseBody));
				JSONArray array = o.getJSONArray("posts");
				v = new PostView[array.size()];
				for (int i = 0; i < array.size(); i++)
				{
					JSONObject sub = array.getJSONObject(i);
					Post p = new Post();
					p.context = PostActivity.this;
					p.username = sub.getString("username");
					p.floor = sub.getInteger("floor");
					p.time = sub.getInteger("time");
					p.content = sub.getString("content");
					p.signature = sub.getString("signature");
					p.avatarFormat = sub.getString("avatarFormat");
					p.memberId = sub.getInteger("memberId");
					p.postId = sub.getInteger("postId");
					p.setAvatarView();
					/*JSONObject prefer = sub.getJSONObject("preferences");
					Log.d(TAG, "prefer: " + prefer);
					if (!prefer.isEmpty())
					{
						Log.d(TAG, "Dealing prefer");
						p.preferences.signature = prefer.getString("signature");
					}*/
					v[i] = new PostView(PostActivity.this, p);
				}
				postList.setAdapter(new BaseAdapter()
				{
					@Override
					public int getCount()
					{
						return v.length;
					}

					@Override
					public Object getItem(int position)
					{
						return v[position];
					}

					@Override
					public long getItemId(int position)
					{
						return v[position].post.postId;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent)
					{
						return v[position];
					}
				});
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{

			}
		});
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
