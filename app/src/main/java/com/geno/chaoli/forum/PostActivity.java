package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.Post;
import com.geno.chaoli.forum.meta.PostView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class PostActivity extends Activity
{
	/* TODO: 2016/3/5 0005 1502 Reply function maybe extended as a full activity.
	 * TODO: DO NOT HARDCODE.
	 */

	public static final int menu_settings = 0;
	public static final int menu_share = 1;
	public static final int menu_author_only = 2;
	public static final int menu_star = 3;

	public Button replySubmit;

	public EditText replyMsg;

	public static LinearLayout postList;

	public static SharedPreferences sp;
	public SharedPreferences.Editor e;

	public int conversationId;

	public String intentToPage;

	public static AsyncHttpClient client = new AsyncHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity);
		postList = (LinearLayout) findViewById(R.id.postList);
		replyMsg = (EditText) findViewById(R.id.reply);
		replySubmit = (Button) findViewById(R.id.replySubmit);
		Bundle data = getIntent().getExtras();
		conversationId = data.getInt("conversationId");
		intentToPage = data.getString("intentToPage");
		sp = getSharedPreferences(Constants.postSP + conversationId, MODE_PRIVATE);
		e = sp.edit();
		replyMsg.setText(sp.getString(getString(R.string.reply), ""));
		replyMsg.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				e.putString(getString(R.string.reply), s.toString()).apply();
			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});
		client.get(this, Constants.postListURL + "/" + conversationId + intentToPage, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				JSONObject o = JSON.parseObject(new String(responseBody));
				JSONArray array = o.getJSONArray("posts");
				for (int i = 0; i < array.size(); i++)
				{
					JSONObject sub = array.getJSONObject(i);
					Post p = new Post();
					p.username = sub.getString("username");
					p.floor = sub.getInteger("floor");
					p.time = sub.getInteger("time");
					p.content = sub.getString("content");
					postList.addView(new PostView(PostActivity.this, p));
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{

			}
		});
		replySubmit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(PostActivity.this, "Reply", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, menu_settings, R.string.settings).setIcon(android.R.drawable.ic_menu_manage);
		menu.add(Menu.NONE, Menu.NONE, menu_share, R.string.share).setIcon(android.R.drawable.ic_menu_share);
		menu.add(Menu.NONE, Menu.NONE, menu_author_only, R.string.author_only).setIcon(android.R.drawable.ic_menu_view);
		menu.add(Menu.NONE, Menu.NONE, menu_star, R.string.star).setIcon(R.drawable.ic_menu_star);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		super.onMenuItemSelected(featureId, item);
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
										Toast.makeText(PostActivity.this, R.string.ignore_this, Toast.LENGTH_SHORT).show();
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
				share.putExtra(Intent.EXTRA_TEXT, Constants.postSP + conversationId);
				share.setType("text/plain");
				startActivity(Intent.createChooser(share, getString(R.string.share)));
				break;
			case menu_author_only:
				finish();
				Intent author_only = new Intent(PostActivity.this, PostActivity.class);
				author_only.putExtra("conversationId", conversationId);
				author_only.putExtra("intentToPage", "?author=lz");
				startActivity(author_only);
				break;
			case menu_star:
				break;
		}
		return true;
	}
}
