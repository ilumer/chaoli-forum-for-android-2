package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

public class AvatarView extends Drawable
{
	public Context context;
	public String image;

	public AvatarView(Context context)
	{
		this(context, "");
	}

	public AvatarView(Context context, String image)
	{
		this.context = context;
		this.image = image;
	}

	@Override
	public void draw(Canvas canvas)
	{

	}

	@Override
	public void setAlpha(int alpha)
	{

	}

	@Override
	public void setColorFilter(ColorFilter colorFilter)
	{

	}

	@Override
	public int getOpacity()
	{
		return 0;
	}
}
