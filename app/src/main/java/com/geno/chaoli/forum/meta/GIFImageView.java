package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

@Deprecated
public class GIFImageView extends View
{
	public Movie movie;

	public long movieStart;

	public GIFImageView(Context context, byte[] imageStream)
	{
		this(context, (AttributeSet) null);
		setGIFImageView(imageStream);
	}

	public GIFImageView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public GIFImageView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public void setGIFImageView(byte[] imageStream)
	{
		movie = Movie.decodeByteArray(imageStream, 0, imageStream.length);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		long now = SystemClock.uptimeMillis();
		movieStart = movieStart == 0 ? now : movieStart;
		if (movie != null)
		{
			int realTime = (int) ((now - movieStart) % movie.duration());
			movie.setTime(realTime);
			movie.draw(canvas, getWidth() - movie.width(), getHeight() - movie.height());
			this.invalidate();
		}
	}
}
