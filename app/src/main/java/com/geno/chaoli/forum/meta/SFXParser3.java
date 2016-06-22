package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.geno.chaoli.forum.PostActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SFXParser3
{
	// Finally, I decide to use this way.

	private static final String TAG = "SFXParser3";

	public static final SpannableStringBuilder parse(final Context context, final TextView textView, String string)
	{
		SpannableStringBuilder spannable = new SpannableStringBuilder(string);
//		tagDealer(s, "[b]", "[/b]", new StyleSpan(Typeface.BOLD));
//		tagDealer(s, "[i]", "[/i]", new StyleSpan(Typeface.ITALIC));

		Matcher c = Pattern.compile("(?<=\\[c=)(.+?)(?=\\[/c\\])").matcher(spannable);
		while (c.find())
		{
			String[] inner = c.group().split("]", 2);
			int color = Color.parseColor(inner[0]);
			spannable.setSpan(new ForegroundColorSpan(color), c.start(), c.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(c.end(), c.end() + 4, "");
			spannable.replace(c.start() - 3, c.start() + inner[0].length() + 1, "");
			c = Pattern.compile("(?<=\\[c=)(.+?)(?=\\[/c\\])").matcher(spannable);
		}

		Matcher url = Pattern.compile("(?<=\\[url=)(.+?)(?=\\[/url\\])").matcher(spannable);
		while (url.find())
		{
			String[] inner = url.group().split("]", 2);
			final String site = inner[0];
			spannable.setSpan(new ClickableSpan()
			{
				@Override
				public void onClick(View widget)
				{
					if (site.startsWith("https://chaoli.club/index.php/")) // temporary
						context.startActivity(new Intent(context, PostActivity.class).putExtra("a", site.substring(30)));
					else
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(site)));
				}
			}, url.start(), url.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(url.end(), url.end() + 6, "");
			spannable.replace(url.start() - 5, url.start() + inner[0].length() + 1, "");
			url = Pattern.compile("(?<=\\[url=)(.+?)(?=\\[/url\\])").matcher(spannable);
		}

		Matcher curtain = Pattern.compile("(?<=\\[curtain\\])(.+?)(?=\\[/curtain\\])").matcher(spannable);
		while (curtain.find())
		{
			spannable.setSpan(new BackgroundColorSpan(Color.BLACK), curtain.start(), curtain.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//			spannable.setSpan(new Touchable, curtain.start(), curtain.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(curtain.end(), curtain.end() + 10, "");
			spannable.replace(curtain.start() - 9, curtain.start(), "");
			curtain = Pattern.compile("(?<=\\[curtain\\])(.+?)(?=\\[/curtain\\])").matcher(spannable);
		}

		Matcher b = Pattern.compile("(?<=\\[b\\])(.+?)(?=\\[/b\\])").matcher(spannable);
		while (b.find())
		{
			spannable.setSpan(new StyleSpan(Typeface.BOLD), b.start(), b.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(b.end(), b.end() + 4, "");
			spannable.replace(b.start() - 3, b.start(), "");
			b = Pattern.compile("(?<=\\[b\\])(.+?)(?=\\[/b\\])").matcher(spannable);
		}

		Matcher i = Pattern.compile("(?<=\\[i\\])(.+?)(?=\\[/i\\])").matcher(spannable);
		while (i.find())
		{
			spannable.setSpan(new StyleSpan(Typeface.ITALIC), i.start(), i.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(i.end(), i.end() + 4, "");
			spannable.replace(i.start() - 3, i.start(), "");
			i = Pattern.compile("(?<=\\[i\\])(.+?)(?=\\[/i\\])").matcher(spannable);
		}

		Matcher u = Pattern.compile("(?<=\\[u\\])(.+?)(?=\\[/u\\])").matcher(spannable);
		while (u.find())
		{
			spannable.setSpan(new UnderlineSpan(), u.start(), u.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(u.end(), u.end() + 4, "");
			spannable.replace(u.start() - 3, u.start(), "");
			u = Pattern.compile("(?<=\\[u\\])(.+?)(?=\\[/u\\])").matcher(spannable);
		}

		Matcher s = Pattern.compile("(?<=\\[s\\])(.+?)(?=\\[/s\\])").matcher(spannable);
		while (s.find())
		{
			spannable.setSpan(new StrikethroughSpan(), s.start(), s.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(s.end(), s.end() + 4, "");
			spannable.replace(s.start() - 3, s.start(), "");
			s = Pattern.compile("(?<=\\[s\\])(.+?)(?=\\[/s\\])").matcher(spannable);
		}

		Matcher center = Pattern.compile("(?<=\\[center\\])(.+?)(?=\\[/center\\])").matcher(spannable);
		while (center.find())
		{
			spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), center.start(), center.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(center.end(), center.end() + 9, "\n\n");
			spannable.replace(center.start() - 8, center.start(), "\n\n");
			center = Pattern.compile("(?<=\\[center\\])(.+?)(?=\\[/center\\])").matcher(spannable);
		}

		Matcher h = Pattern.compile("(?<=\\[h\\])(.+?)(?=\\[/h\\])").matcher(spannable);
		while (h.find())
		{
			spannable.setSpan(new RelativeSizeSpan(1.3f), h.start(), h.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(h.end(), h.end() + 4, "\n\n");
			spannable.replace(h.start() - 3, h.start(), "\n\n");
			h = Pattern.compile("(?<=\\[h\\])(.+?)(?=\\[/h\\])").matcher(spannable);
		}

		Matcher img = Pattern.compile("(?<=\\[img\\])(.+?)(?=\\[/img\\])").matcher(spannable);
		while (img.find())
		{
			final int start = img.start(), end = img.end();
			Log.d(TAG, "parse: " + start + ", " + end + ": " + spannable.subSequence(start, end).toString());
			Glide.with(context).load(spannable.subSequence(start, end).toString()).asBitmap().into(new SimpleTarget<Bitmap>()
			{
				@Override
				public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
				{
					spannable.setSpan(new ImageSpan(context, resource), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
					Log.d(TAG, "onResourceReady: Inner: " + resource);
					textview.setText(spannable);
				}
			});
		}

		return spannable;
	}
//
//	public static final void tagDealer(final SpannableStringBuilder s, String openTag, String closeTag, Object what)
//	{
//		Matcher matcher = Pattern.compile("(?<=" + openTag + ")(.+?)(?=" + closeTag + ")").matcher(s);
//		while (matcher.find())
//		{
//			s.setSpan(what, matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//			s.replace(matcher.end() - 1, matcher.end() + closeTag.length() - 1, "");
//			s.replace(matcher.start() - openTag.length() + 1, matcher.start() + 1, "");
////			s.replace(matcher.start() - openTag.length(), matcher.start(), "");
////			s.replace(matcher.end() - openTag.length(), matcher.end() - openTag.length() + closeTag.length(), "");
//			matcher = Pattern.compile("(?<=" + openTag + ")(.+?)(?=" + closeTag + ")").matcher(s);
//		}
//	}
}
