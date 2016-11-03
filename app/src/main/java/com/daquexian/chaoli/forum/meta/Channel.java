package com.daquexian.chaoli.forum.meta;

import android.content.Context;

import com.daquexian.chaoli.forum.ChaoliApplication;
import com.daquexian.chaoli.forum.R;

public enum Channel
{
	caff(R.string.channel_caff, 1, false, 0xFFA0A0A0),
	ad(R.string.channel_ad, 3, false, 0xFF999999),
	maths(R.string.channel_maths, 4, true, 0xFF673AB7),
	physics(R.string.channel_physics, 5, true, 0xFFFF5722),
	chem(R.string.channel_chem, 6, true, 0xFFF44336),
	biology(R.string.channel_biology, 7, true, 0xFF4CAF50),
	tech(R.string.channel_tech, 8, true, 0xFF2196F3),
	test(R.string.channel_test, 9, false, 0xFF607D8B),
	admin(R.string.channel_admin, 24, false, 0xFFEAEAEA),
	court(R.string.channel_court, 25, true, 0xFFE040D0),
	recycled(R.string.channel_recycled, 27, false, 0xFFA6B3E0),
	announ(R.string.channel_announ, 28, true, 0xFF999999),
	others(R.string.channel_others, 30, true, 0xFF3F5185),
	socsci(R.string.channel_socsci, 34, true, 0xFFE04000),
	lang(R.string.channel_lang, 40, true, 0xFF9030C0);

	private int name;
	private int detail;
	private boolean isGuestVisible;
	private int channelId;
	private int color;

	Channel(int name, int channelId, boolean isGuestVisible, int color)
	{
		this.name = name;
		this.detail = name + 1;
		this.channelId = channelId;
		this.isGuestVisible = isGuestVisible;
		this.color = color;
	}

	public String getDetail(Context context)
	{
		return context.getString(detail);
	}

	public int getChannelId()
	{
		return channelId;
	}

	public int getColor()
	{
		return color;
	}

	// FIXME: 2016/2/4 0252 A function that will never be used.
	public boolean isGuestVisible()
	{
		return isGuestVisible;
	}

	@Override
	public String toString()
	{
		return ChaoliApplication.getAppContext().getString(this.name);
	}

	@Deprecated
	public String toString(Context context)
	{
		return context.getString(this.name);
	}


	public static final Channel getChannel(int channelId)
	{
		for (Channel c : Channel.values())
			if (c.getChannelId() == channelId) return c;
		return null;
	}

	public static final Channel getChannel(String channelName)
	{
		for (Channel c : Channel.values())
			if (c.toString().equals(channelName)) return c;
		return null;
	}
}
