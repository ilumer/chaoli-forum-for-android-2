package com.geno.chaoli.forum.meta;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

@Deprecated
public class DrawableTextView extends Drawable
{
	@Override
	public int getOpacity()
	{
		return 0;
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter)
	{

	}

	@Override
	public void setColorFilter(int color, PorterDuff.Mode mode)
	{
		super.setColorFilter(color, mode);
	}

	@Override
	public void draw(Canvas canvas)
	{

	}

	@Override
	public void setAlpha(int alpha)
	{

	}
}
