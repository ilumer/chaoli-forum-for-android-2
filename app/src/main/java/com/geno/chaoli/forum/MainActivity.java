package com.geno.chaoli.forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationView;

import net.simonvt.menudrawer.MenuDrawer;

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

	public LinearLayout slidingMenu;

	public RelativeLayout.LayoutParams slidingMenuParam;

	private float xDown, xMove, xUp;
	private VelocityTracker vt;
	private float vX;
	private boolean menuIsShown = false;

	public static final int VELOCITY = 400;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		sp = getSharedPreferences(Constants.conversationSP, MODE_PRIVATE);
		e = sp.edit();

		initFragments();
		initSlidingMenu();


	}

	private void initSlidingMenu()
	{
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
		userName.setText("Username");
		slidingMenu.addView(userName, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout loginBtn = new LinearLayout(this);
		loginBtn.setOrientation(LinearLayout.HORIZONTAL);
		ImageView loginImg = new ImageView(this);
		loginImg.setImageResource(R.mipmap.ic_menu_login);
		TextView loginStr = new TextView(this);
		loginStr.setText("Login");
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
		MenuDrawer m = MenuDrawer.attach(this);
		m.setMenuView(slidingMenu);
		m.setContentView(R.layout.main_activity);
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
//		for (int i = 0; i < frag.length; i++)
//		{
//			(frag[i] = new ConversationListFragment()).setChannel(Channel.getChannel(mainFragmentsTitles.get(i)).name());
//			mainFragments.add(frag[i]);
//		}

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
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		Log.d(TAG, "onTouchEvent() called with: " + "event = [" + event + "]");
//		acquireVelocityTracker(event);
//		switch (event.getAction())
//		{
//			case MotionEvent.ACTION_DOWN:
//				xDown = event.getRawX();
//				break;
//			case MotionEvent.ACTION_MOVE:
//				xMove = event.getRawX();
//				isScrollToShowMenu();
//				break;
//			case MotionEvent.ACTION_UP:
//				xUp = event.getRawX();
//				isShowMenu();
//				releaseVelocityTracker();
//				break;
//			case MotionEvent.ACTION_CANCEL:
//				releaseVelocityTracker();
//				break;
//		}
//		return true;
//	}
//
//	private void isScrollToShowMenu()
//	{
//		Log.d(TAG, "isScrollToShowMenu() called with: " + "");
//		int distX = (int) (xMove - xDown);
//		if (menuIsShown) scrollToHideMenu(distX);
//		else scrollToShowMenu(distX);
//	}
//
//	private void scrollToHideMenu(int scrollX)
//	{
//		Log.d(TAG, "scrollToHideMenu() called with: " + "scrollX = [" + scrollX + "]");
//		if (scrollX > 0 && scrollX <= slidingMenuParam.width)
//			slidingMenuParam.leftMargin = scrollX - slidingMenuParam.width;
//		slidingMenu.setLayoutParams(slidingMenuParam);
//	}
//
//	private void scrollToShowMenu(int scrollX)
//	{
//		Log.d(TAG, "scrollToShowMenu() called with: " + "scrollX = [" + scrollX + "]");
//		if (scrollX < 0 && scrollX >= - slidingMenuParam.width)
//			slidingMenuParam.leftMargin = scrollX;
//		slidingMenu.setLayoutParams(slidingMenuParam);
//	}
//
//	private void isShowMenu()
//	{
//		Log.d(TAG, "isShowMenu() called with: " + "");
//		vX = getScrollVelocity();
//		// TODO: 16-3-12 2021 Waiting for
//		if (shouldHideMenu()) hideMenu();
//		else showMenu();
//	}
//
//	private void showMenu()
//	{
//		Log.d(TAG, "showMenu() called with: " + "");
//		new showMenuAsyncTask().execute(50);
//		menuIsShown = true;
//	}
//
//	private void hideMenu()
//	{
//		Log.d(TAG, "hideMenu() called with: " + "");
//		new showMenuAsyncTask().execute(-50);
//		menuIsShown = false;
//	}
//
//	private void acquireVelocityTracker(MotionEvent event)
//	{
//		Log.d(TAG, "acquireVelocityTracker() called with: " + "event = [" + event + "]");
//		if (vt == null) vt = VelocityTracker.obtain();
//		vt.addMovement(event);
//	}
//
//	private void releaseVelocityTracker()
//	{
//		Log.d(TAG, "releaseVelocityTracker() called with: " + "");
//		vt.clear();
//		vt.recycle();
//		vt = null;
//	}
//
//	private int getScrollVelocity()
//	{
//		Log.d(TAG, "getScrollVelocity() called with: " + "");
//		vt.computeCurrentVelocity(1000);
//		return (int) Math.abs(vt.getXVelocity());
//	}
//
//	private boolean wantToShowMenu()
//	{
//		return !menuIsShown && xUp - xDown > 0;
//	}
//
//	private boolean wantToHideMenu()
//	{
//		return menuIsShown && xDown - xUp > 0;
//	}
//
//	private boolean shouldShowMenu()
//	{
//		Log.d(TAG, "shouldShowMenu() called with: " + "");
//		return xUp - xDown > slidingMenuParam.width / 2 || vX > VELOCITY;
//	}
//
//	private boolean shouldHideMenu()
//	{
//		Log.d(TAG, "shouldHideMenu() called with: " + "");
//		return xDown - xUp > slidingMenuParam.width / 2 || vX > VELOCITY;
//	}
//
//	class showMenuAsyncTask extends AsyncTask<Integer, Integer, Integer>
//	{
//		private static final String TAG = "showMenuAsyncTask";
//		@Override
//		protected Integer doInBackground(Integer... params)
//		{
//			Log.d(TAG, "doInBackground() called with: " + "params = [" + params + "]");
//			int leftMargin = slidingMenuParam.leftMargin;
//			while (true)
//			{
//				leftMargin += params[0];
//				if (params[0] > 0 && leftMargin > 0)
//				{
//					leftMargin = 0;
//					break;
//				}
//				else if (params[0] < 0 && leftMargin < - slidingMenuParam.width)
//				{
//					leftMargin = -slidingMenuParam.width;
//					break;
//				}
//				publishProgress(leftMargin);
//				try
//				{
//					Thread.sleep(40);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			return leftMargin;
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values)
//		{
//			Log.d(TAG, "onProgressUpdate() called with: " + "values = [" + values + "]");
//			super.onProgressUpdate(values);
//			slidingMenuParam.leftMargin = values[0];
//			slidingMenu.setLayoutParams(slidingMenuParam);
//		}
//
//		@Override
//		protected void onPostExecute(Integer integer)
//		{
//			Log.d(TAG, "onPostExecute() called with: " + "integer = [" + integer + "]");
//			super.onPostExecute(integer);
//			slidingMenuParam.leftMargin = integer;
//			slidingMenu.setLayoutParams(slidingMenuParam);
//		}
//	}
}
