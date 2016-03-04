package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.Methods;

import java.lang.ref.WeakReference;

public class ApplicationHandler extends Handler
{
	public static final String TAG = "ApplicationHandler";

	public static final int FINISH_CONVERSATION_LIST_ANALYSIS = 1;

	public WeakReference<Context> context;

	public View toBeDealWith;

	public Activity activity;

	public SharedPreferences sp;

	public ApplicationHandler(Context context)
	{
		this.context = new WeakReference<>(context);
	}

	public ApplicationHandler(Context context, View toBeDealWith, Activity activity, SharedPreferences sp)
	{
		this.context = new WeakReference<>(context);
		this.toBeDealWith = toBeDealWith;
		this.activity = activity;
		this.sp = sp;
	}

	@Override
	public void handleMessage(Message msg)
	{
		super.handleMessage(msg);
		Context hContext = context.get();
		Log.v(TAG, hContext + "");
		switch (msg.what)
		{
			case FINISH_CONVERSATION_LIST_ANALYSIS:
				Log.v(TAG, "Deal UI start");
				ConversationListFragment.deal(context.get(), activity, (LinearLayout) toBeDealWith, sp);
				Log.v(TAG, "Deal UI finish");
				break;
		}
	}

	/*
	public static class MainHandler extends Handler
	{
		WeakReference<MainActivity> mMainActivity;

		public MainHandler(MainActivity activity)
		{
			mMainActivity = new WeakReference<>(activity);
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
					for (ConversationView c : Methods.dealConversationList(activity, activity.sp.getString("listJSON", "")))
					{
						((LinearLayout) activity.findViewById(R.id.conversationList)).addView(c);
					}
					Log.v(TAG, "deal UI finish");
					break;
			}
		}
	}*/
}
