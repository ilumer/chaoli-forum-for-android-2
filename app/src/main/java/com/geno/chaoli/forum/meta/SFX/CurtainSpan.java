package com.geno.chaoli.forum.meta.SFX;

import android.os.Parcel;
import android.text.style.BackgroundColorSpan;

public class CurtainSpan extends BackgroundColorSpan
{
	public CurtainSpan(int color)
	{
		super(color);
	}

	public CurtainSpan(Parcel src)
	{
		super(src);
	}
}
