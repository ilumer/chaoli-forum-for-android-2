package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.Methods;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity
{
	public static final String TAG = "MainActivity";

	public ConversationView[] c;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public ViewPager mainPager;
	public PagerTabStrip mainTabStrip;
	public List<Fragment> mainFragments;
	public List<String> mainFragementsTitles;

	public static class MainHandler extends Handler
	{
		WeakReference<Activity> mainActivity;

		public MainHandler(Activity activity)
		{
			mainActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case Constants.FINISH_CONVERSATION_LIST_ANALYSIS:
					ConversationListFragment.deal();
			}
		}
	}

	public Handler mainHandler = new MainHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences(Constants.conversationSP, MODE_PRIVATE);
		e = sp.edit();
		setContentView(R.layout.activity_main);
		mainPager = (ViewPager) findViewById(R.id.mainPager);
		mainTabStrip = (PagerTabStrip) findViewById(R.id.mainTabStrip);
		mainTabStrip.setDrawFullUnderline(false);
		mainTabStrip.setHorizontalScrollBarEnabled(true);

		mainFragments = new ArrayList<>();

		mainFragementsTitles = new ArrayList<>();
		mainFragementsTitles.add(Channel.maths.toString());
		mainFragementsTitles.add(Channel.physics.toString());
		mainFragementsTitles.add(Channel.chem.toString());
		mainFragementsTitles.add(Channel.biology.toString());
		mainFragementsTitles.add(Channel.tech.toString());
		mainFragementsTitles.add(Channel.court.toString());
		mainFragementsTitles.add(Channel.announ.toString());
		mainFragementsTitles.add(Channel.others.toString());
		mainFragementsTitles.add(Channel.socsci.toString());
		mainFragementsTitles.add(Channel.lang.toString());

		ConversationListFragment[] frag = new ConversationListFragment[10];

		boolean loggedIn = false;

		Channel[] available = new Channel[loggedIn ? 14 : 10];
		int loopCnt = 0;

		for (int i = 0; i < 14; i++)
		{
			if (Channel.values()[i].isGuestVisible())
			{
				available[loopCnt] = Channel.values()[i];
				loopCnt++;
			}
		}

		for (int i = 0; i < 10; i++)
		{
			frag[i] = new ConversationListFragment();
			frag[i].setChannel(available[i].name());
			mainFragments.add(frag[i]);
		}

		mainPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public android.support.v4.app.Fragment getItem(int position)
			{
				return mainFragments.get(position);
			}

			@Override
			public int getCount()
			{
				return mainFragments.size();
			}

			@Override
			public CharSequence getPageTitle(int position)
			{
				return mainFragementsTitles.get(position);
			}
		});
	}
}
