package com.geno.chaoli.forum.meta;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SFXParser
{
	public static final char token = '\ufff0';

	private static final String TAG = "SFXParser";

	public static Map<String, Map<Object, Integer[]>> parse_MAP_OBJECT_INTEGER(String string)
	{
		Map<Object, Integer[]> spans = new HashMap<>();
		Map<String, Map<Object, Integer[]>> result = new HashMap<>();


		result.put(string, spans);
		return result;
	}

	public static Map<String, Object> parse_MAP_STRING_OBJECT(String string)
	{
		Map<String, Object> spans = new HashMap<>();
		String temp = "";
		Object o = null;
		boolean flag_PARSE = true;
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			if (c == '[' && flag_PARSE)
			{
				int dest = string.indexOf("]", i);
				String tag = string.substring(i, dest);
				i = dest;
				Log.d(TAG, "parse_MAP_STRING_OBJECT: " + tag);
				if (tag.startsWith("[url="))
				{

				}
				else if (tag.startsWith("[quote="))
				{

				}
				else if (tag.startsWith("[c="))
				{
					String color = tag.substring(3);
					o = new ForegroundColorSpan(Color.parseColor(color));
				}
				else switch (tag)
				{
					case "[code]":
						flag_PARSE = false;
						spans.put(temp, o);
						break;
					case "[/code]":
						flag_PARSE = true;
						spans.put(temp, o);
						break;
					case "[/c]":
						spans.put(temp, o);
						break;
				}
			}
			else
			{
				temp += c;
			}
		}
		return spans;
	}

	public static SpannableString getSpan_FROM_OBJECT_INTEGER(Map<String, Map<Object, Integer>> map)
	{
		SpannableString string = new SpannableString((String) map.keySet().toArray()[0]);
		return string;
	}

}
