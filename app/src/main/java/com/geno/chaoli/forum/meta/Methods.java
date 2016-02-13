package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.widget.TextView;

public class Methods
{
	public Channel getChannel(int channelId)
	{
		for (Channel c : Channel.values())
			if (c.getChannelId() == channelId) return c;
		return null;
	}

	public static TextView ChannelView(Context context, Channel channel)
	{
		TextView channelView = new TextView(context);
		int textColor;
		switch (channel.getChannelId())
		{
			case 4: textColor = 0xFF4030A0; break;
			case 5: textColor = 0xFFA00020; break;
			case 6: textColor = 0xFFE04000; break;
			case 7: textColor = 0xFF008000; break;
			case 8: textColor = 0xFF0040D0; break;
			case 9: textColor = 0xFF202020; break;
			case 36: textColor = 0xFFE04000; break;
			case 40: textColor = 0xFF9030C0; break;
			default: textColor = 0xFFA0A0A0;
		}
		channelView.setTextColor(textColor);
		channelView.setText(channel.toString());
		return channelView;
	}
}
