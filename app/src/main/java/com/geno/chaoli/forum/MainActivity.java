package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

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
	public List<String> mainFragmentsTitles;

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
					break;
				case Constants.FINISH_LOGIN:
					Toast.makeText(mainActivity.get(), "Finish Login", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.main_activity);
		mainPager = (ViewPager) findViewById(R.id.mainPager);
		mainTabStrip = (PagerTabStrip) findViewById(R.id.mainTabStrip);
		mainTabStrip.setDrawFullUnderline(false);
		mainTabStrip.setHorizontalScrollBarEnabled(true);

		mainFragments = new ArrayList<>();

		boolean loggedIn = sp.getBoolean(Constants.loginBool, false);

		mainFragmentsTitles = new ArrayList<>();

		if (loggedIn) mainFragmentsTitles.add(Channel.caff.toString());
		mainFragmentsTitles.add(Channel.maths.toString());
		mainFragmentsTitles.add(Channel.physics.toString());
		mainFragmentsTitles.add(Channel.chem.toString());
		mainFragmentsTitles.add(Channel.biology.toString());
		mainFragmentsTitles.add(Channel.tech.toString());
		mainFragmentsTitles.add(Channel.court.toString());
		mainFragmentsTitles.add(Channel.announ.toString());
		mainFragmentsTitles.add(Channel.others.toString());
		mainFragmentsTitles.add(Channel.socsci.toString());
		mainFragmentsTitles.add(Channel.lang.toString());

		ConversationListFragment[] frag = new ConversationListFragment[loggedIn ? 11 : 10];

		for (int i = 0; i < frag.length; i++)
		{
			(frag[i] = new ConversationListFragment()).setChannel(Methods.getChannel(mainFragmentsTitles.get(i)).name());
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
				return mainFragmentsTitles.get(position);
			}
		});
	}
}
