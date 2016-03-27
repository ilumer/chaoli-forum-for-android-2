package com.geno.chaoli.forum;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import com.geno.chaoli.forum.meta.Channel;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
	public static final String TAG = "MainActivity";

	private NavigationDrawerFragment fragment;

	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.drawer);
		title = getTitle();

		fragment.setUp(R.id.drawer, (DrawerLayout) findViewById(R.id.drawer_main));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position)
	{
		FragmentManager fm = getFragmentManager();
		ConversationListFragment c = new ConversationListFragment().setChannel(getChannel(position));
		fm.beginTransaction().replace(R.id.main_view, c).commit();
	}

	public String getChannel(int position)
	{
		String[] channel =
		new String[]
				{
						Channel.maths.toString(),
						Channel.physics.toString(),
						Channel.chem.toString(),
						Channel.biology.toString(),
						Channel.tech.toString(),
						Channel.court.toString(),
						Channel.announ.toString(),
						Channel.others.toString(),
						Channel.socsci.toString(),
						Channel.lang.toString(),
				};
		return channel[position];
	}


	/*	public static final String TAG = "MainActivity";

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public ViewPager mainPager;
	public PagerTabStrip mainTabStrip;
	public List<Fragment> mainFragments;
	public List<String> mainFragmentsTitles;

	public LinearLayout slidingMenu;

	public RelativeLayout.LayoutParams slidingMenuParam;

	public MenuDrawer m;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		m = MenuDrawer.attach(this, Position.LEFT);
		//setContentView(R.layout.main_activity);
		sp = getSharedPreferences(Constants.conversationSP, MODE_PRIVATE);
		e = sp.edit();

		m.setContentView(R.layout.main_activity);
		initFragments();

		initSlidingMenu();
		m.setMenuView(slidingMenu);


	}

	private void initSlidingMenu()
	{
		// TODO: 2016/3/17 0017 0033 Some of these lines are unused. 
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		slidingMenu = new LinearLayout(this);
		slidingMenu.setBackgroundColor(0x80000000);
		slidingMenu.setLayoutParams(new RelativeLayout.LayoutParams((int) (dm.widthPixels / 2.5), ViewGroup.LayoutParams.MATCH_PARENT));
		slidingMenu.setOrientation(LinearLayout.VERTICAL);


		RelativeLayout avatarBox = new RelativeLayout(this);
		avatarBox.setGravity(RelativeLayout.CENTER_IN_PARENT);
		ImageView avatar = new ImageView(this);
		avatarBox.addView(avatar);
		slidingMenu.addView(avatarBox, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		TextView userName = new TextView(this);
		userName.setText(R.string.username);
		slidingMenu.addView(userName, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout loginBtn = new LinearLayout(this);
		loginBtn.setOrientation(LinearLayout.HORIZONTAL);
		ImageView loginImg = new ImageView(this);
		loginImg.setImageResource(R.mipmap.ic_menu_login);
		TextView loginStr = new TextView(this);
		loginStr.setText(R.string.login);
		loginBtn.addView(loginImg);
		loginBtn.addView(loginStr);
		loginBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
			}
		});
		slidingMenu.addView(loginBtn);

		slidingMenuParam = (RelativeLayout.LayoutParams) slidingMenu.getLayoutParams();
		slidingMenuParam.leftMargin = 0;

		slidingMenu.setLayoutParams(slidingMenuParam);
	}

	private void initFragments()
	{
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

		if (loggedIn) mainFragments.add(new ConversationListFragment().setChannel(Channel.caff.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.maths.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.physics.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.chem.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.biology.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.tech.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.court.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.announ.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.others.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.socsci.name()));
		mainFragments.add(new ConversationListFragment().setChannel(Channel.lang.name()));

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
	}*/
}
