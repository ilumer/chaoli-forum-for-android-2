package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.geno.chaoli.forum.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LaTeXtView extends TextView
{
	private String text;

	private SpannableString span;

	private View v;

	public static final String SITE = "http://latex.codecogs.com/gif.latex?\\dpi{" + 440 / 2 + "} ";

	public static final Pattern PATTERN1 = Pattern.compile("(?<=\\$)(.+?)(?=\\$)");

	public static final Pattern PATTERN2 = Pattern.compile("(?<=\\\\\\()(.+?)(?=\\\\\\))");

	public static final String TAG = "LaTeXtView";

	public LaTeXtView(Context context)
	{
		super(context);
		v = View.inflate(context, R.layout.latextview, null);
	}

	public LaTeXtView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public LaTeXtView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public void setText(final String text)
	{
		Log.d(TAG, "setText: " + text + " start.");
		v = View.inflate(getContext(), R.layout.latextview, null);
		this.text = text;
		span = new SpannableString(text);
		((TextView) findViewById(R.id.content)).setText(text);
		boolean flag = false;
		Matcher m1 = PATTERN1.matcher(text);
		while (m1.find())
		{
			if (flag = !flag)
			{
				final int start = m1.start() - 1, end = m1.end() + 1;
				Glide.with(getContext()).load(SITE + m1.group()).asBitmap().into(new SimpleTarget<Bitmap>()
				{
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
					{
						Log.d(TAG, "onResourceReady: executing " + text.substring(start, end));
						span.setSpan(new CenteredImageSpan(getContext(), resource), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						((TextView) findViewById(R.id.content)).setText(span);
					}
				});
			}
		}
		Matcher m2 = PATTERN2.matcher(text);
		while (m2.find())
		{
			final int start = m2.start() - 2, end = m2.end() + 2;
			Glide.with(getContext()).load(SITE + m2.group()).asBitmap().into(new SimpleTarget<Bitmap>()
			{
				@Override
				public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
				{
					Log.d(TAG, "onResourceReady: executing " + text.substring(start, end));
					span.setSpan(new CenteredImageSpan(getContext(), resource), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
					((TextView) findViewById(R.id.content)).setText(span);
				}
			});
		}
	}

	@Override
	public String toString()
	{
		return text;
	}

	public SpannableString getSpan()
	{
		return span;
	}
}
