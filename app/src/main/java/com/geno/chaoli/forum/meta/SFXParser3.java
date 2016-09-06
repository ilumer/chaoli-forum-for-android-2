package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;

import com.geno.chaoli.forum.PostActivity;
import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.model.Post;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SFXParser3 {
	// Finally, I decide to use this way.

	private static final String TAG = "SFXParser3";

	private static final String[] iconStrs = new String[]{"/:)", "/:D", "/^b^", "/o.o", "/xx", "/#", "/))", "/--", "/TT", "/==",
			"/.**", "/:(", "/vv", "/$$", "/??", "/:/", "/xo", "/o0", "/><", "/love",
			"/...", "/XD", "/ii", "/^^", "/<<", "/>.", "/-_-", "/0o0", "/zz", "/O!O",
			"/##", "/:O", "/<", "/heart", "/break", "/rose", "/gift", "/bow", "/moon", "/sun",
			"/coin", "/bulb", "/tea", "/cake", "/music", "/rock", "/v", "/good", "/bad", "/ok",
			"/asnowwolf-smile", "/asnowwolf-laugh", "/asnowwolf-upset", "/asnowwolf-tear",
			"/asnowwolf-worry", "/asnowwolf-shock", "/asnowwolf-amuse"};
	private static final int[] icons = new int[]{R.drawable.emoticons__0050_1, R.drawable.emoticons__0049_2, R.drawable.emoticons__0048_3, R.drawable.emoticons__0047_4,
			R.drawable.emoticons__0046_5, R.drawable.emoticons__0045_6, R.drawable.emoticons__0044_7, R.drawable.emoticons__0043_8, R.drawable.emoticons__0042_9,
			R.drawable.emoticons__0041_10, R.drawable.emoticons__0040_11, R.drawable.emoticons__0039_12, R.drawable.emoticons__0038_13, R.drawable.emoticons__0037_14,
			R.drawable.emoticons__0036_15, R.drawable.emoticons__0035_16, R.drawable.emoticons__0034_17, R.drawable.emoticons__0033_18, R.drawable.emoticons__0032_19,
			R.drawable.emoticons__0031_20, R.drawable.emoticons__0030_21, R.drawable.emoticons__0029_22, R.drawable.emoticons__0028_23, R.drawable.emoticons__0027_24,
			R.drawable.emoticons__0026_25, R.drawable.emoticons__0025_26, R.drawable.emoticons__0024_27, R.drawable.emoticons__0023_28, R.drawable.emoticons__0022_29,
			R.drawable.emoticons__0021_30, R.drawable.emoticons__0020_31, R.drawable.emoticons__0019_32, R.drawable.emoticons__0018_33, R.drawable.emoticons__0017_34,
			R.drawable.emoticons__0016_35, R.drawable.emoticons__0015_36, R.drawable.emoticons__0014_37, R.drawable.emoticons__0013_38, R.drawable.emoticons__0012_39,
			R.drawable.emoticons__0011_40, R.drawable.emoticons__0010_41, R.drawable.emoticons__0009_42, R.drawable.emoticons__0008_43, R.drawable.emoticons__0007_44,
			R.drawable.emoticons__0006_45, R.drawable.emoticons__0005_46, R.drawable.emoticons__0004_47, R.drawable.emoticons__0003_48, R.drawable.emoticons__0002_49,
			R.drawable.emoticons__0001_50, R.drawable.asonwwolf_smile, R.drawable.asonwwolf_laugh, R.drawable.asonwwolf_upset, R.drawable.asonwwolf_tear,
			R.drawable.asonwwolf_worry, R.drawable.asonwwolf_shock, R.drawable.asonwwolf_amuse};

	public static SpannableStringBuilder parse(final Context context, String string, List<Post.Attachment> attachmentList) {
		final SpannableStringBuilder spannable = new SpannableStringBuilder(string);

		Pattern cPattern = Pattern.compile("(?i)\\[c=(.*?)](.*?)\\[/c]");
		Matcher c = cPattern.matcher(spannable);
		while (c.find()) {
			int color = Color.parseColor(c.group(1));
			spannable.setSpan(new ForegroundColorSpan(color), c.start(), c.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

			spannable.replace(c.end(2), c.end(), "");
			spannable.replace(c.start(), c.start(2), "");
			c = cPattern.matcher(spannable);
		}

		Pattern urlPattern = Pattern.compile("(?i)\\[url=(.*?)](.*?)\\[/url]");
		Matcher url = urlPattern.matcher(spannable);
		while (url.find()) {
			//String[] inner = url.group().split("]", 2);
			//final String site = inner[0];
			final String site = url.group(1);
			spannable.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					if (site.startsWith("https://chaoli.club/index.php/")) // temporary
						context.startActivity(new Intent(context, PostActivity.class).putExtra("a", site.substring(30)));
					else {
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(site)));
					}
				}
			}, url.start(2), url.end(2), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(url.end(2), url.end(), "");
			spannable.replace(url.start(), url.start(2), "");
			url = urlPattern.matcher(spannable);
		}

		Pattern curtainPattern = Pattern.compile("(?i)(?<=\\[curtain\\])((.|\\n)+?)(?=\\[/curtain\\])");
		Matcher curtain = curtainPattern.matcher(spannable);
		while (curtain.find()) {
			spannable.setSpan(new BackgroundColorSpan(Color.DKGRAY), curtain.start(), curtain.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//			spannable.setSpan(new Touchable, curtain.start(), curtain.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(curtain.end(), curtain.end() + 10, "");
			spannable.replace(curtain.start() - 9, curtain.start(), "");
			curtain = Pattern.compile("(?i)(?<=\\[curtain\\])(.+?)(?=\\[/curtain\\])").matcher(spannable);
		}

		Pattern bPattern = Pattern.compile("(?i)\\[b]((.|\\n)+?)\\[/b]");
		Matcher b = bPattern.matcher(spannable);
		while (b.find()) {
			spannable.setSpan(new StyleSpan(Typeface.BOLD), b.start(1), b.end(1), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(b.end(1), b.end(), "");
			spannable.replace(b.start(), b.start(1), "");
			b = bPattern.matcher(spannable);
		}

		Pattern iPattern = Pattern.compile("(?i)(?<=\\[i\\])((.|\\n)+?)(?=\\[/i\\])");
		Matcher i = iPattern.matcher(spannable);
		while (i.find()) {
			spannable.setSpan(new StyleSpan(Typeface.ITALIC), i.start(), i.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(i.end(), i.end() + 4, "");
			spannable.replace(i.start() - 3, i.start(), "");
			i = iPattern.matcher(spannable);
		}

		Pattern uPattern = Pattern.compile("(?i)(?<=\\[u\\])((.|\\n)+?)(?=\\[/u\\])");
		Matcher u = uPattern.matcher(spannable);
		while (u.find()) {
			spannable.setSpan(new UnderlineSpan(), u.start(), u.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(u.end(), u.end() + 4, "");
			spannable.replace(u.start() - 3, u.start(), "");
			u = uPattern.matcher(spannable);
		}

		Pattern sPattern = Pattern.compile("(?i)(?<=\\[s\\])((.|\\n)+?)(?=\\[/s\\])");
		Matcher s = sPattern.matcher(spannable);
		while (s.find()) {
			spannable.setSpan(new StrikethroughSpan(), s.start(), s.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(s.end(), s.end() + 4, "");
			spannable.replace(s.start() - 3, s.start(), "");
			s = sPattern.matcher(spannable);
		}

		Pattern centerPattern = Pattern.compile("(?i)(?<=\\[center\\])((.|\\n)+?)(?=\\[/center\\])");
		Matcher center = centerPattern.matcher(spannable);
		while (center.find()) {
			spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), center.start(), center.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(center.end(), center.end() + 9, "\n\n");
			spannable.replace(center.start() - 8, center.start(), "\n\n");
			center = centerPattern.matcher(spannable);
		}

		Pattern hPattern = Pattern.compile("(?i)(?<=\\[h\\])((.|\\n)+?)(?=\\[/h\\])");
		Matcher h = hPattern.matcher(spannable);
		while (h.find()) {
			spannable.setSpan(new RelativeSizeSpan(1.3f), h.start(), h.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannable.replace(h.end(), h.end() + 4, "\n\n");
			spannable.replace(h.start() - 3, h.start(), "\n\n");
			h = hPattern.matcher(spannable);
		}


		Pattern attachmentPattern = Pattern.compile("(?i)\\[attachment:(.*?)]");
		Matcher attachmentM = attachmentPattern.matcher(spannable);
		while (attachmentList != null && attachmentM.find()) {
			for (int j = attachmentList.size() - 1; j >= 0; j--) {
				Post.Attachment attachment = attachmentList.get(j);
				if (attachment.getAttachmentId().equals(attachmentM.group(1))) {
					if (!(attachment.getFilename().endsWith(".jpg") || attachment.getFilename().endsWith(".png"))) {
						try {
							final String finalUrl = "https://chaoli.club/index.php/attachment/" + attachment.getAttachmentId() + "_" + URLEncoder.encode(attachment.getFilename(), "UTF-8");
							spannable.replace(attachmentM.start(), attachmentM.end(), attachment.getFilename());
							spannable.setSpan(new ClickableSpan() {
								@Override
								public void onClick(View view) {
									context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)));
								}
							}, attachmentM.start(), attachmentM.start() + attachment.getFilename().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
							attachmentM = attachmentPattern.matcher(spannable);
						} catch (UnsupportedEncodingException e) {
							Log.w(TAG, "parse: ", e);
						}
					}
					break;
				}
			}
		}

		String str = spannable.toString();
		for (int j = 0; j < iconStrs.length; j++) {
			int from = 0;
			String iconStr = iconStrs[j];
			while ((from = str.indexOf(iconStr, from)) >= 0) {
				if (("/<".equals(iconStr) && str.substring(from).startsWith("/<<") || ("/#".equals(iconStr) && str.substring(from).startsWith("/##")))) {
					from++;
					continue;
				}
				spannable.setSpan(new ImageSpan(context, icons[j]), from, from + iconStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
				from += iconStr.length();
			}
		}
		return spannable;
	}
}
