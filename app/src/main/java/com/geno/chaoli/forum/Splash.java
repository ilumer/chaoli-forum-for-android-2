package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//TODO Any splash screen here?
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				try
				{
					sleep(0);
					startActivity(new Intent(Splash.this, Class.forName("com.geno.chaoli.forum.MainActivity")));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					finish();
				}
			}
		};
		t.start();
	}
}
