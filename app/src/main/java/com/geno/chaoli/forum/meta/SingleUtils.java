package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.geno.chaoli.forum.MainActivity;

public class SingleUtils
{
	private int dpi;

	private static SingleUtils ourInstance = new SingleUtils();

	public static SingleUtils getInstance()
	{
		return ourInstance;
	}

	private SingleUtils()
	{
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) new MainActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		dpi = dm.densityDpi;
	}

	public int getDpi()
	{
		return dpi;
	}
}
