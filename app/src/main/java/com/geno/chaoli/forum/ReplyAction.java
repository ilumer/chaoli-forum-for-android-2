package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ReplyAction extends AppCompatActivity
{
	public static final String TAG = "ReplyAction";

	public int conversationId;

	public AsyncHttpClient client;

	public String replyMsg;

	public EditText replyText;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public static final int menu_draft = 0;
	public static final int menu_reply = 1;
	public static final int menu_purge = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_action);
		setTitle(R.string.reply);
		client = new AsyncHttpClient();
		sp = getSharedPreferences("draftList", MODE_PRIVATE);
		e = sp.edit();
		Bundle data = getIntent().getExtras();
		conversationId = data.getInt("conversationId");
		replyMsg = data.getString("replyMsg", "");
		replyText = (EditText) findViewById(R.id.replyText);
		replyText.setText(sp.getString("replyText", "") + replyMsg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, menu_draft, R.string.save_as_draft).setIcon(android.R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, Menu.NONE, menu_reply, R.string.reply).setIcon(R.drawable.ic_cab_done_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, Menu.NONE, menu_purge, R.string.purge).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getOrder())
		{
			case menu_draft:
				e.putBoolean(conversationId + "", true);
				e.commit();
				getSharedPreferences(conversationId + "", MODE_PRIVATE).edit()
						.putString("replyText", replyText.getText().toString()).commit();
				Toast.makeText(ReplyAction.this, R.string.save_as_draft, Toast.LENGTH_SHORT).show();
				finish();
				break;
			case menu_reply:
				Map<String, String> requestParam = new HashMap<>();
				requestParam.put("conversationId", conversationId + "");
				requestParam.put("content", replyText.getText().toString());
				requestParam.put("userId", "3013");
				requestParam.put("token", "a321073f737aa");
				client.post(Constants.replyURL + "/" + conversationId, new RequestParams(requestParam), new AsyncHttpResponseHandler()
				{
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
					{
						Toast.makeText(ReplyAction.this, "Hello", Toast.LENGTH_SHORT).show();
						String resp = "";
						for (int i = 0; i < responseBody.length; i++)
						{
							resp += responseBody[i];
						}
						String head = "";
						for (int i = 0; i < headers.length; i++)
						{
							head += headers[i].toString() + "\n";
						}
						Log.d(TAG, "onSuccess: " + resp.length() + resp);
						Log.d(TAG, "onSuccess: " + head.length() + head);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
					{
						Toast.makeText(ReplyAction.this, "Hi", Toast.LENGTH_SHORT).show();
					}
				});
				break;
			case menu_purge:
				getSharedPreferences(conversationId + "", MODE_PRIVATE).edit().clear().apply();
				break;
		}
		return true;
	}
}
