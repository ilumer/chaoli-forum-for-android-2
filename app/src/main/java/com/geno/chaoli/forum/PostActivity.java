package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	public Button replySubmit;

	public EditText replyMsg;

	public static LinearLayout postList;

	public static SharedPreferences sp;
	public SharedPreferences.Editor e;

	public static AsyncHttpClient client = new AsyncHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences(Constants.postSP, MODE_PRIVATE);
		e = sp.edit();
		setContentView(R.layout.post_activity);
		postList = (LinearLayout) findViewById(R.id.postList);
		replyMsg = (EditText) findViewById(R.id.reply);
		replySubmit = (Button) findViewById(R.id.replySubmit);
		Bundle data = getIntent().getExtras();
		int conversationId = data.getInt("conversationId");
		client.get(this, Constants.postListURL + "/" + conversationId, new AsyncHttpResponseHandler()
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
}
