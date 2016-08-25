package com.geno.chaoli.forum.model;

import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.ConversationState;

import java.util.List;
import java.util.StringTokenizer;

public class Conversation
{
	private int conversationId;
	private Channel channel;
	private int channelId;
	private String title;
	private String firstPost;
	private String link;
	private String startMemberId;
	private String lastPostMemberId;
	private String startMember;
	@JSONField(name="startMemberAvatarFormat")
	private String startMemberAvatarSuffix;
	@JSONField(name="lastPostMemberAvatarFormat")
	private String lastPostMemberAvatarSuffix;
	private Drawable startAvatar;
	private String lastPostMember;
	private Drawable lastPostAvatar;
	private String lastPostTime;
	private int replies;

	private List<ConversationState> state;

	public String getFirstPost() {
		return firstPost;
	}

	public void setFirstPost(String firstPost) {
		this.firstPost = firstPost;
	}

	public int getConversationId()
	{
		return conversationId;
	}
	public void setConversationId(int conversationId)
	{
		this.conversationId = conversationId;
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

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getStartMemberId() {
		return startMemberId;
	}

	public void setStartMemberId(String startMemberId) {
		this.startMemberId = startMemberId;
	}

	public String getLastPostMemberId() {
		return lastPostMemberId;
	}

	public void setLastPostMemberId(String lastPostMemberId) {
		this.lastPostMemberId = lastPostMemberId;
	}

	public String getStartMember()
	{
		return startMember;
	}

	public void setStartMember(String startMember)
	{
		this.startMember = startMember;
	}

	public Drawable getStartAvatar()
	{
		return startAvatar;
	}

	public void setStartAvatar(Drawable startAvatar)
	{
		this.startAvatar = startAvatar;
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

	public String getStartMemberAvatarSuffix() {
		return startMemberAvatarSuffix;
	}

	public void setStartMemberAvatarSuffix(String startMemberAvatarSuffix) {
		this.startMemberAvatarSuffix = startMemberAvatarSuffix;
	}

	public String getLastPostMemberAvatarSuffix() {
		return lastPostMemberAvatarSuffix;
	}

	public void setLastPostMemberAvatarSuffix(String lastPostMemberAvatarSuffix) {
		this.lastPostMemberAvatarSuffix = lastPostMemberAvatarSuffix;
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

	public List<ConversationState> getState()
	{
		return state;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public void setState(List<ConversationState> state)
	{
		this.state = state;
	}
}
