package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
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

import io.github.kexanie.library.MathView;

public class LaTeXtView extends TextView
{
/*	private String text;

	private SpannableString span;

	private View v;*/
	private String mText;
	private SpannableString mSpan;

	private OnRenderListener mListener;

	public static final String SITE = "http://latex.codecogs.com/gif.latex?\\dpi{" + 440 / 2 + "}";

	//public static final Pattern PATTERN1 = Pattern.compile("(?<=\\$)(.+?)(?=\\$)");

	//public static final Pattern PATTERN2 = Pattern.compile("(?<=\\\\\\()(.+?)(?=\\\\\\))");
	private static final Pattern PATTERN1 = Pattern.compile("\\$\\$?([^\\$]+?)\\$?\\$");
	private static final Pattern PATTERN2 = Pattern.compile("\\\\\\((.*?)\\\\\\)");
	private static final Pattern PATTERN3 = Pattern.compile("\\\\begin\\{.*?\\}(.|\\n)*?\\\\end\\{.*?\\}");

	public static final String TAG = "LaTeXtView";

	public LaTeXtView(Context context)
	{
		super(context);
		//v = View.inflate(context, R.layout.latextview, null);
	}

	public LaTeXtView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public LaTeXtView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public void setText(final Context context, String text){
		text = removeSpacesInFormula(text);
		mText = text;
		SpannableStringBuilder builder = SFXParser3.parse(context, text);
		setText(builder);

		retrieveLaTeXImg(builder);
	}


	private void retrieveLaTeXImg(final SpannableStringBuilder builder) {
		String text = builder.toString();
		Log.d(TAG, "retrieveLaTeXImg: " + text);

		Matcher m1 = PATTERN1.matcher(text);
		Matcher m2 = PATTERN2.matcher(text);
		Matcher m3 = PATTERN3.matcher(text);

		_retrieveLaTeXImg(builder, m1, m2, m3);
	}
	private void _retrieveLaTeXImg(final SpannableStringBuilder builder, final Matcher m1, final Matcher m2, final Matcher m3) {
		String formula;
		Boolean flag2 = false, flag3;
		if ((flag3 = m3.find()) || (flag2 = m2.find()) || m1.find()) {
			int start, end;
			if (flag3) {
				start = m3.start();
				end = m3.end();
				formula = m3.group();
			} else if (flag2) {
				start = m2.start();
				end = m2.end();
				formula = m2.group(1);
			} else {
				start = m1.start();
				end = m1.end();
				formula = m1.group(1);
			}
			final int fStart = start, fEnd = end;
			Glide.with(getContext()).load(SITE + formula).asBitmap().into(new SimpleTarget<Bitmap>()
			{
				@Override
				public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
				{
					Log.d(TAG, "onResourceReady: executing " + mText.substring(fStart, fEnd));
					builder.setSpan(new CenteredImageSpan(getContext(), resource), fStart, fEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
					setText(builder);
					//((TextView) findViewById(R.id.content)).setText(builder);
					_retrieveLaTeXImg(builder, m1, m2, m3);
				}
				@Override
				public void onLoadFailed(Exception e, Drawable errorDrawable) {
					super.onLoadFailed(e, errorDrawable);
					e.printStackTrace();
					_retrieveLaTeXImg(builder, m1, m2, m3);
				}
			});
		}

		if (mListener != null){
			mListener.onComplete();
		}
	}

	private String removeSpacesInFormula(String str){
		Matcher m1 = PATTERN1.matcher(str);
		Matcher m2 = PATTERN2.matcher(str);
		Matcher m3 = PATTERN3.matcher(str);
		Boolean flag3, flag2 = false;
		// remove all spaces, codecogs returns error if formula contains spaces
		while ((flag3 = m3.find()) || (flag2 = m2.find()) || m1.find()){
			String oldStr;
			if (flag3) oldStr = m3.group();
			else if (flag2) oldStr = m2.group(1);
			else oldStr = m1.group(1);
			String newStr = oldStr.replaceAll(" ", "").replaceAll("\\n", "");
			str = str.replace(oldStr, newStr);
		}

		return str;
	}


	public void setListener(OnRenderListener listener){
		mListener = listener;
	}

	interface OnRenderListener {
		void onComplete();
	}

	@Override
	public String toString()
	{
		return mText;
	}

	public SpannableString getSpan()
	{
		return mSpan;
	}
}
