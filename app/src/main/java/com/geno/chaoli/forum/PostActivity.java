package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.Methods;
import com.geno.chaoli.forum.meta.PostView;

import java.lang.ref.WeakReference;

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

	public static class PostHandler extends Handler
	{
		WeakReference<Activity> postActivity;

		public PostHandler(Activity activity)
		{
			postActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case Constants.FINISH_POST_LIST_ANALYSIS:
					for (PostView p : Methods.dealPostList(postActivity.get(), sp.getString(Constants.postSPKey, "")))
					{
						postList.addView(p);
					}
			}
		}
	}

	public PostHandler postHandler = new PostHandler(this);

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
		Methods.getPostList(this, "/" + conversationId);

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
