package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class SFXParser2
{
	private static final String TAG = "SFXParser2";

	public static final Map<String, Object> parse_MAP_STRING_OBJECT(String string)
	{
		Map<String, Object> spans = new LinkedHashMap<>();
		String temp = "";
		int l = string.length();
		Object o = null;
		for (int i = 0; i < l; i++)
		{
			char c = string.charAt(i);
			if (c == '[') // Start of an open tag
			{
				spans.put(temp, null);
				temp = "";
				int openEnd = string.indexOf("]", i); // End of an open tag
				int close, closeEnd;
				if (string.substring(i, openEnd + 1).equals("[code]"))
				{
					close = string.indexOf("[/code]", openEnd);
					closeEnd = string.indexOf("[/code]", close) + 6;
				}
				else
				{
					close = string.indexOf("[/", openEnd); // Start of an close tag
					closeEnd = string.indexOf("]", close); // End of an close tag
				}
				if (openEnd > l || close > l || closeEnd > l
					|| openEnd < 0 || close < 0 || closeEnd < 0)
				{
					temp += c;
					continue;
				}
				// Then:
				//  ---------------------------------------------- i
				// /                        ---------------------- openEnd + 1
				// |                       /               ------- close
				// |                       |              /     -- closeEnd + 1
				// |                       |              |    /
				// |                       |              |    |
				// v                       v              v    v
				// [url=https://chaoli.club] Chaoli Forum [/url]
				/*Log.d(TAG, "i = " + i
						+  ", openEnd = " + openEnd
						+  ", close = " + close
						+  ", closeEnd = " + closeEnd
						+  ". \n" +
						   "subStr0: " + string.substring(i, openEnd + 1)
						+  "\nsubStr1: " + string.substring(openEnd + 1, close)
						+  "\nsubStr2: " + string.substring(close, closeEnd + 1));*/
				String tag = string.substring(i, openEnd + 1);
				String cleanTag = tag.substring(1, tag.length() - 1);
				String text = string.substring(openEnd + 1, close);
				String closeTag = string.substring(close, closeEnd + 1);
				Log.d(TAG, "parse_MAP_STRING_OBJECT: " + text);
				switch (cleanTag.split("=")[0])
				{
					case "b":
						spans.put(text, new StyleSpan(Typeface.BOLD));
						break;
					case "i":
						spans.put(text, new StyleSpan(Typeface.ITALIC));
						break;
					case "u":
						spans.put(text, new UnderlineSpan());
						break;
					case "s":
						spans.put(text, new StrikethroughSpan());
						break;
					default:
						temp += c;
				}
				i = closeEnd;
			}
			else temp += c;
		}
		spans.put(temp, null);
		return spans;
	}

	public static final SpannableString getSpan_FROM_STRING_OBJECT(Map<String, Object> map)
	{
		SpannableString string = new SpannableString("");
		for (Map.Entry<String, Object> entry : map.entrySet())
		{
			SpannableString temp = new SpannableString(entry.getKey());
			temp.setSpan(entry.getValue(), 0, temp.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			string = SFXUtils.SpannableStringUtils.appendString(string, temp);
		}
		return string;
	}
}
