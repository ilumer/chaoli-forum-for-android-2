package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class ChannelTextView extends TextView
{
	private Channel channel;

	public static final String TAG = "ChannelTextView";

	ChannelTextView(Context context, Channel channel)
	{
		this(context);
		this.channel = channel;
		this.setPadding(5, 5, 5, 5);
		this.setText(channel.toString());
		this.setTextColor(channel.getColor());
	}

	ChannelTextView(Context context)
	{
		this(context, (AttributeSet) null);
	}

	ChannelTextView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	ChannelTextView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(this.getChannel().getColor());
		canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel = channel;
	}
}
