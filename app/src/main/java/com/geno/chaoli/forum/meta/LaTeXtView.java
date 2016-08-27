package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.OnClick;

public class LaTeXtView extends TextView
{
	private Context mContext;
	private String mText;
	private SpannableStringBuilder mSpannableStringBuilder;

	private OnCompleteListener mListener;

	public static final String SITE = "http://latex.codecogs.com/gif.latex?\\dpi{" + 440 / 2 + "}";

	//public static final Pattern PATTERN1 = Pattern.compile("(?<=\\$)(.+?)(?=\\$)");

	//public static final Pattern PATTERN2 = Pattern.compile("(?<=\\\\\\()(.+?)(?=\\\\\\))");
	private static final Pattern PATTERN1 = Pattern.compile("\\$\\$?(([^\\$]|\\n)+?)\\$?\\$");
	private static final Pattern PATTERN2 = Pattern.compile("\\\\[(\\[]((.|\\n)*?)\\\\[\\])]");
	private static final Pattern IMG_PATTERN = Pattern.compile("\\[img](.*?)\\[/img]");
	//private static final Pattern PATTERN3 = Pattern.compile("\\\\begin\\{.*?\\}(.|\\n)*?\\\\end\\{.*?\\}");

	public static final String TAG = "LaTeXtView";

	public LaTeXtView(Context context)
	{
		super(context);
		init(context);
	}

	public LaTeXtView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public LaTeXtView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void setText(String text){
		text = removeNewlineInFormula(text);
		text += '\n';
		//Log.d(TAG, "setText: " + text);
		mText = text;
		SpannableStringBuilder builder = SFXParser3.parse(mContext, text);
		setText(builder);

		retrieveLaTeXImg(builder);
	}

	public void setText(String text, OnCompleteListener listener) {
		setListener(listener);
		setText(text);
	}

	private void retrieveLaTeXImg(final SpannableStringBuilder builder) {
		String text = builder.toString();

		Matcher m1 = PATTERN1.matcher(text);
		Matcher m2 = PATTERN2.matcher(text);
		Matcher imgMatcher = IMG_PATTERN.matcher(text);

		_retrieveLaTeXImg(builder, m1, m2, imgMatcher);
	}
	private void _retrieveLaTeXImg(final SpannableStringBuilder builder, final Matcher m1, final Matcher m2, final Matcher imgMatcher) {
		String formula;
		Boolean flag1 = false, flag2 = false, flagImg = false;
		if ((flagImg = imgMatcher.find()) || (flag1 = m1.find()) || (flag2 = m2.find())) {
			int start, end;
			if (flagImg) {
				start = imgMatcher.start();
				end = imgMatcher.end();
				formula = imgMatcher.group(1);
			} else if (flag2) {
				start = m2.start();
				end = m2.end();
				formula = m2.group(1);//.replaceAll("[ \\t\\r\\n]", "");
			} else {
				start = m1.start();
				end = m1.end();
				formula = m1.group(1);//.replaceAll("[ \\t\\r\\n]", "");
			}
			Log.d(TAG, "_retrieveLaTeXImg: " + formula);
			try {
				formula = URLEncoder.encode(formula, "UTF-8");
				final int fStart = start, fEnd = end;
				String url = flagImg ? formula : SITE + formula;
				Glide.with(getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>()
				{
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
					{
						builder.setSpan(new CenteredImageSpan(getContext(), resource), fStart, fEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						setText(builder);
						_retrieveLaTeXImg(builder, m1, m2, imgMatcher);
					}
					@Override
					public void onLoadFailed(Exception e, Drawable errorDrawable) {
						super.onLoadFailed(e, errorDrawable);
						e.printStackTrace();
						_retrieveLaTeXImg(builder, m1, m2, imgMatcher);
					}
				});
			} catch (UnsupportedEncodingException e){

			}
		} else {
			mSpannableStringBuilder = builder;
			if (mListener != null) {
				mListener.onComplete(builder);
			}
		}
	}

	private String removeNewlineInFormula(String str){
		Matcher m1 = PATTERN1.matcher(str);
		Matcher m2 = PATTERN2.matcher(str);
		Boolean flag3 = false, flag2 = false, flag1 = false;
		// remove all spaces, codecogs returns error if formula contains spaces
		while ((flag1 = m1.find()) || (flag2 = m2.find())) {
			String oldStr;
			if (flag2) oldStr = m2.group();
			else oldStr = m1.group();
			String newStr = oldStr.replaceAll("[\\n\\r]", "");
			str = str.replace(oldStr, newStr);
		}

		//Log.d(TAG, "removeNewlineInFormula: str = " + str);
		return str;
	}


	public void setListener(OnCompleteListener listener){
		mListener = listener;
	}

	public interface OnCompleteListener {
		void onComplete(SpannableStringBuilder spannableStringBuilder);
	}

	@Override
	public String toString()
	{
		return mText;
	}

	public SpannableStringBuilder getSpannableStringBuilder(){
		return mSpannableStringBuilder;
	}

	private void init(Context context) {
		mContext = context;
	}
}
