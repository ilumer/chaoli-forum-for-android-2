package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.Methods;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity
{
	public static final String TAG = "MainActivity";

	public ConversationView[] c;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public static class MainHandler extends Handler
	{
		WeakReference<MainActivity> mMainActivity;

		public MainHandler(MainActivity activity)
		{
			mMainActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			MainActivity activity = mMainActivity.get();
			switch (msg.what)
			{
				case 0:
					// TODO: 2016/2/23 0023 1405 Update UI
					Log.v(TAG, "deal UI start");
					for (ConversationView c : Methods.dealList(activity, activity.sp.getString("listJSON", "")))
					{
						((LinearLayout) activity.findViewById(R.id.mainView)).addView(c);

					}
					break;
			}
		}
	}

	public MainHandler mainHandler = new MainHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("conversationList", MODE_PRIVATE);
		e = sp.edit();
		setContentView(R.layout.activity_main);
		Methods.getList(this);
	}
}
