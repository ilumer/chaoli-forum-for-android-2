package com.geno.chaoli.forum.meta.SFX;

import android.text.SpannableString;

import com.geno.chaoli.forum.meta.SFXUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorSFX
{
	public boolean checkSFX(CharSequence c)
	{
		return c.toString().contains("\\[/c\\]");
	}

	public CharSequence execSFX(CharSequence orig)
	{
		CharSequence clean = SFXUtils.removeAllSFX(orig.toString());
		SpannableString span = new SpannableString(clean);
		Matcher color = Pattern.compile("(?<=\\[c=)(.+?)(?=\\])").matcher(orig);
		Matcher coloredText = Pattern.compile("(?<=\\])(.+?)(?=\\[/c\\])").matcher(orig);
		return null;
	}
}
