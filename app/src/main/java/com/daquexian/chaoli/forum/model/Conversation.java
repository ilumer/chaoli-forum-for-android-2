package com.daquexian.chaoli.forum.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.daquexian.chaoli.forum.BR;
import com.daquexian.chaoli.forum.binding.DiffItem;
import com.google.gson.annotations.SerializedName;

public class Conversation extends BaseObservable implements DiffItem, Comparable<Conversation>,Parcelable {
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
	private String startTime;
	@SerializedName("lastPostMemberAvatarFormat")
	private String lastPostMemberAvatarSuffix;
	private String lastPostMember;
	private String lastPostTime;
	private int replies;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

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

	/**
	 * 对于主题帖来说，最新的排在最前，不同于单个主题帖中的楼层，最先发表的排在最前
	 * @param o 另一个主题帖
	 * @return 比较结果
     */
	@Override
	public int compareTo(Conversation o) {
		return o.getLastPostTime().compareTo(getLastPostTime());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.conversationId);
		dest.writeInt(this.channelId);
		dest.writeString(this.title);
		dest.writeString(this.firstPost);
		dest.writeString(this.link);
		dest.writeString(this.startMemberId);
		dest.writeString(this.lastPostMemberId);
		dest.writeString(this.startMember);
		dest.writeString(this.startMemberAvatarSuffix);
		dest.writeString(this.lastPostMemberAvatarSuffix);
		dest.writeString(this.lastPostMember);
		dest.writeString(this.lastPostTime);
		dest.writeInt(this.replies);
		dest.writeString(this.startTime);
	}

	public Conversation() {
	}

	protected Conversation(Parcel in) {
		this.conversationId = in.readInt();
		this.channelId = in.readInt();
		this.title = in.readString();
		this.firstPost = in.readString();
		this.link = in.readString();
		this.startMemberId = in.readString();
		this.lastPostMemberId = in.readString();
		this.startMember = in.readString();
		this.startMemberAvatarSuffix = in.readString();
		this.lastPostMemberAvatarSuffix = in.readString();
		this.lastPostMember = in.readString();
		this.lastPostTime = in.readString();
		this.replies = in.readInt();
		this.startTime = in.readString();
	}

	public static final Parcelable.Creator<Conversation> CREATOR = new Parcelable.Creator<Conversation>() {
		@Override
		public Conversation createFromParcel(Parcel source) {
			return new Conversation(source);
		}

		@Override
		public Conversation[] newArray(int size) {
			return new Conversation[size];
		}
	};
}
