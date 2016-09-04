package com.geno.chaoli.forum.model;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class Post
{
	public Context context;
	public int postId;
	public int conversationId;
	public int memberId;
	public long time;
	public int editMemberId;
	public long editTime;
	public int deleteMemberId;
	public long deleteTime;
	public String title;
	public String content;
	public int floor;
	public String username;
	public String avatarFormat;
	public Map<Integer, String> groups;
	public String groupNames;
	public String signature;
	public List<Attachment> attachments;

	//public AvatarView avatarView;

	public Post(){}
/*
	public Post(int postId, int conversationId,
			int memberId, long time,
			int deleteMemberId, long deleteTime)

	public Post(int postId, int conversationId,
			int memberId, long time,
			int editMemberId, long editTime,
			String title, String content,
			int floor,
			String username, String avatarFormat,
			Map<Integer, String> groups, String groupNames)
	{
		this(postId, conversationId, memberId, time, editMemberId, editTime,
				title, content, floor, username, avatarFormat, groups, groupNames,
				null);
	}

	public Post(int postId, int conversationId,
			int memberId, long time,
			int editMemberId, long editTime,
			String title, String content,
			int floor,
			String username, String avatarFormat,
			Map<Integer, String> groups, String groupNames,
			List<Attachment> attachments)
	{
		this(postId, conversationId, memberId, time, editMemberId, editTime, 0, 0,
				title, content, floor, username, avatarFormat, groups, groupNames,
				attachments);
	}
*/
	public Post(Context context, int postId, int conversationId,
			int memberId, long time,
			int editMemberId, long editTime,
			int deleteMemberId, long deleteTime,
			String title, String content,
			int floor,
			String username, String avatarFormat,
			@Nullable Map<Integer, String> groups, @Nullable String groupNames,
			@Nullable String signature,
			@Nullable List<Attachment> attachments)
	{
		this.context = context;
		this.postId = postId;
		this.conversationId = conversationId;
		this.memberId = memberId;
		this.time = time;
		this.editMemberId = editMemberId;
		this.editTime = editTime;
		this.deleteMemberId = deleteMemberId;
		this.deleteTime = deleteTime;
		this.title = title;
		this.content = content;
		this.floor = floor;
		this.username = username;
		this.avatarFormat = avatarFormat;
		this.groups = groups;
		this.groupNames = groupNames;
		this.signature = signature;
		this.attachments = attachments;
		//this.avatarView = new AvatarView(context, avatarFormat, memberId, username);
	}

	public int getPostId()
	{
		return postId;
	}

	public void setPostId(int postId)
	{
		this.postId = postId;
	}

	public int getConversationId()
	{
		return conversationId;
	}

	public void setConversationId(int conversationId)
	{
		this.conversationId = conversationId;
	}

	public int getMemberId()
	{
		return memberId;
	}

	public void setMemberId(int memberId)
	{
		this.memberId = memberId;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public int getEditMemberId()
	{
		return editMemberId;
	}

	public void setEditMemberId(int editMemberId)
	{
		this.editMemberId = editMemberId;
	}

	public long getEditTime()
	{
		return editTime;
	}

	public void setEditTime(long editTime)
	{
		this.editTime = editTime;
	}

	public int getDeleteMemberId()
	{
		return deleteMemberId;
	}

	public void setDeleteMemberId(int deleteMemberId)
	{
		this.deleteMemberId = deleteMemberId;
	}

	public long getDeleteTime()
	{
		return deleteTime;
	}

	public void setDeleteTime(long deleteTime)
	{
		this.deleteTime = deleteTime;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public int getFloor()
	{
		return floor;
	}

	public void setFloor(int floor)
	{
		this.floor = floor;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getAvatarFormat()
	{
		return avatarFormat;
	}

	public void setAvatarFormat(String avatarFormat)
	{
		this.avatarFormat = avatarFormat;
	}

	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments)
	{
		this.attachments = attachments;
	}

	/*public void setAvatarView(AvatarView avatarView)
	{
		this.avatarView = avatarView;
	}

	public void setAvatarView()
	{
		this.avatarView = new AvatarView(context, avatarFormat, memberId, username);
	}*/

	/*public AvatarView getAvatarView()
	{
		return avatarView;
	}*/

	public class Attachment
	{
		public String attachmentId;
		public String filename;
		public String secret;
		public int postId;
		public int draftMemberId;
		public int draftConversationId;

		public Attachment()
		{
			this("", "", "", 0);
		}

		public Attachment(String attachmentId, String filename, String secret, int postId)
		{
			this(attachmentId, filename, secret, postId, 0, 0);
		}

		public Attachment(String attachmentId, String filename, String secret, int postId,
						  int draftMemberId, int draftConversationId)
		{
			this.attachmentId = attachmentId;
			this.filename = filename;
			this.secret = secret;
			this.postId = postId;
			this.draftMemberId = draftMemberId;
			this.draftConversationId = draftConversationId;
		}

		public String getAttachmentId()
		{
			return attachmentId;
		}

		public void setAttachmentId(String attachmentId)
		{
			this.attachmentId = attachmentId;
		}

		public String getFilename()
		{
			return filename.toLowerCase();
		}

		public void setFilename(String filename)
		{
			this.filename = filename;
		}

		public String getSecret()
		{
			return secret;
		}

		public void setSecret(String secret)
		{
			this.secret = secret;
		}

		public int getPostId()
		{
			return postId;
		}

		public void setPostId(int postId)
		{
			this.postId = postId;
		}

		public int getDraftMemberId()
		{
			return draftMemberId;
		}

		public void setDraftMemberId(int draftMemberId)
		{
			this.draftMemberId = draftMemberId;
		}

		public int getDraftConversationId()
		{
			return draftConversationId;
		}

		public void setDraftConversationId(int draftConversationId)
		{
			this.draftConversationId = draftConversationId;
		}
	}
}
