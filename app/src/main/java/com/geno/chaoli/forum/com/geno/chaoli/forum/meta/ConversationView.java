package com.geno.chaoli.forum.com.geno.chaoli.forum.meta;

import android.graphics.drawable.Drawable;

public class ConversationView
{
	private static final String TAG = "ConversationView";

	public Channel channel;
	public String title;
	public String excerpt;
	public String senderMember;
	public Drawable senderAvatar;
	public String lastPostMember;
	public Drawable lastPostAvatar;
	public String lastPostTime;
	public int replies;
	public boolean isSticky;
	public boolean isFeatured;

	public ConversationView()
	{
		this.channel = null;
		this.title = "";
		this.excerpt = "";
		this.senderMember = "";
		this.senderAvatar = null;
		this.lastPostMember = "";
		this.lastPostAvatar = null;
		this.lastPostTime = "";
		this.replies = 0;
	}

	// TODO: 2016/2/4 0247 What if any avatar is null? (Use a self-made widget.)
	public ConversationView(Channel channel, String title, String excerpt,
			String senderMember, Drawable senderAvatar,
			String lastPostMember, Drawable lastPostAvatar,
			String lastPostTime, int replies)
	{
		this.channel = channel;
		this.title = title;
		this.excerpt = excerpt;
		this.senderMember = senderMember;
		this.senderAvatar = senderAvatar;
		this.lastPostMember = lastPostMember;
		this.lastPostAvatar = lastPostAvatar;
		this.lastPostTime = lastPostTime;
		this.replies = replies;
	}

	public Channel getChannel() {
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
