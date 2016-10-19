package com.geno.chaoli.forum.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.geno.chaoli.forum.BR;
import com.geno.chaoli.forum.binding.DiffItem;
import com.google.gson.annotations.SerializedName;

public class Conversation extends BaseObservable implements DiffItem, Comparable<Conversation>
{
	private int conversationId;
	private int channelId;
	private String title;
	private String firstPost;
	private String link;
	private String startMemberId;
	private String lastPostMemberId;
	private String startMember;
	@SerializedName("startMemberAvatarFormat")
	private String startMemberAvatarSuffix;
	@SerializedName("lastPostMemberAvatarFormat")
	private String lastPostMemberAvatarSuffix;
	private String lastPostMember;
	private String lastPostTime;
	private int replies;

	@Bindable
	public int getConversationId() {
		return conversationId;
	}

	public void setConversationId(int conversationId) {
		this.conversationId = conversationId;
		notifyPropertyChanged(BR.conversationId);
	}

	@Bindable
	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
		notifyPropertyChanged(BR.channelId);
	}

	@Bindable
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		notifyPropertyChanged(BR.title);
	}

	@Bindable
	public String getFirstPost() {
		return firstPost;
	}

	public void setFirstPost(String firstPost) {
		this.firstPost = firstPost;
		notifyPropertyChanged(BR.firstPost);
	}

	@Bindable
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
		notifyPropertyChanged(BR.link);
	}

	@Bindable
	public String getStartMemberId() {
		return startMemberId;
	}

	public void setStartMemberId(String startMemberId) {
		this.startMemberId = startMemberId;
		notifyPropertyChanged(BR.startMemberId);
	}

	@Bindable
	public String getLastPostMemberId() {
		return lastPostMemberId;
	}

	public void setLastPostMemberId(String lastPostMemberId) {
		this.lastPostMemberId = lastPostMemberId;
		notifyPropertyChanged(BR.lastPostMemberId);
	}

	@Bindable
	public String getStartMember() {
		return startMember;
	}

	public void setStartMember(String startMember) {
		this.startMember = startMember;
		notifyPropertyChanged(BR.startMember);
	}

	@Bindable
	public String getStartMemberAvatarSuffix() {
		return startMemberAvatarSuffix;
	}

	public void setStartMemberAvatarSuffix(String startMemberAvatarSuffix) {
		this.startMemberAvatarSuffix = startMemberAvatarSuffix;
		notifyPropertyChanged(BR.startMemberAvatarSuffix);
	}

	@Bindable
	public String getLastPostMemberAvatarSuffix() {
		return lastPostMemberAvatarSuffix;
	}

	public void setLastPostMemberAvatarSuffix(String lastPostMemberAvatarSuffix) {
		this.lastPostMemberAvatarSuffix = lastPostMemberAvatarSuffix;
		notifyPropertyChanged(BR.lastPostMemberAvatarSuffix);
	}

	@Bindable
	public String getLastPostMember() {
		return lastPostMember;
	}

	public void setLastPostMember(String lastPostMember) {
		this.lastPostMember = lastPostMember;
		notifyPropertyChanged(BR.lastPostMember);
	}

	@Bindable
	public String getLastPostTime() {
		return lastPostTime;
	}

	public void setLastPostTime(String lastPostTime) {
		this.lastPostTime = lastPostTime;
		notifyPropertyChanged(BR.lastPostTime);
	}

	@Bindable
	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
		notifyPropertyChanged(BR.replies);
	}

	@Override
	public boolean areContentsTheSame(DiffItem anotherItem) {
		Conversation newConversation = (Conversation) anotherItem;
		return !(this.getFirstPost() == null && newConversation.getFirstPost() != null)
				&& !(this.getFirstPost() != null && newConversation.getFirstPost() == null)
				&& ((this.getFirstPost() == null && newConversation.getFirstPost() == null) || this.getFirstPost().equals(newConversation.getFirstPost()))
				&& this.getReplies() == newConversation.getReplies();
	}

	@Override
	public boolean areItemsTheSame(DiffItem anotherItem) {
		Conversation newConversation = (Conversation) anotherItem;
		return this.getConversationId() == newConversation.getConversationId();
	}

	@Override
	public int compareTo(Conversation o) {
		return getLastPostTime().compareTo(o.getLastPostTime());
	}
}
