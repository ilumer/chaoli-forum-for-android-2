package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ConversationView extends View
{
	private static final String TAG = "ConversationView";

	public static final String namespace = "http://schemas.geno1024.com";

	public Channel channel;
	public String title;
	public String excerpt;
	public String link;
	public String senderMember;
	public Drawable senderAvatar;
	public String lastPostMember;
	public Drawable lastPostAvatar;
	public String lastPostTime;
	public int replies;
	public ConversationState state;

	public ConversationView()
	{
		this(null, null, "", "", "", "", null, "", null, "", 0);
	}

	public ConversationView(Context context, Channel channel, String title, String excerpt, String link,
			String senderMember, Drawable senderAvatar,
			String lastPostMember, Drawable lastPostAvatar,
			String lastPostTime, int replies)
	{
		 this(context, channel, title, excerpt, link, senderMember, senderAvatar, lastPostMember,
				lastPostAvatar, lastPostTime, replies, ConversationState.normal);
	}

	// TODO: 2016/2/4 0247 What if any avatar is null? (Use a self-made widget.)
	public ConversationView(Context context, Channel channel, String title, String excerpt, String link,
			String senderMember, Drawable senderAvatar,
			String lastPostMember, Drawable lastPostAvatar,
			String lastPostTime, int replies, ConversationState state)
	{
		this(context);
		this.channel = channel;
		this.title = title;
		this.excerpt = excerpt;
		this.link = link;
		this.senderMember = senderMember;
		this.senderAvatar = senderAvatar;
		this.lastPostMember = lastPostMember;
		this.lastPostAvatar = lastPostAvatar;
		this.lastPostTime = lastPostTime;
		this.replies = replies;
		this.state = state;
	}

	public ConversationView(Context context)
	{
		this(context, null);
	}

	public ConversationView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ConversationView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		if (attrs != null)
		{
			this.channel = Channel.valueOf(attrs.getAttributeValue(namespace, "channel"));
			this.title = attrs.getAttributeValue(namespace, "title");
			this.excerpt = attrs.getAttributeValue(namespace, "excerpt");
			this.link = attrs.getAttributeValue(namespace, "link");
			this.senderMember = attrs.getAttributeValue(namespace, "senderMember");
		}
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel = channel;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getExcerpt()
	{
		return excerpt;
	}

	public void setExcerpt(String excerpt)
	{
		this.excerpt = excerpt;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getSenderMember()
	{
		return senderMember;
	}

	public void setSenderMember(String senderMember)
	{
		this.senderMember = senderMember;
	}

	public Drawable getSenderAvatar()
	{
		return senderAvatar;
	}

	public void setSenderAvatar(Drawable senderAvatar)
	{
		this.senderAvatar = senderAvatar;
	}

	public String getLastPostMember()
	{
		return lastPostMember;
	}

	public void setLastPostMember(String lastPostMember)
	{
		this.lastPostMember = lastPostMember;
	}

	public Drawable getLastPostAvatar()
	{
		return lastPostAvatar;
	}

	public void setLastPostAvatar(Drawable lastPostAvatar)
	{
		this.lastPostAvatar = lastPostAvatar;
	}

	public String getLastPostTime()
	{
		return lastPostTime;
	}

	public void setLastPostTime(String lastPostTime)
	{
		this.lastPostTime = lastPostTime;
	}

	public int getReplies()
	{
		return replies;
	}

	public void setReplies(int replies)
	{
		this.replies = replies;
	}
}
