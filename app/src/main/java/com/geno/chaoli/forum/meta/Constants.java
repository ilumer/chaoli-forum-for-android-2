package com.geno.chaoli.forum.meta;

public class Constants
{
	public static final int paddingLeft = 16;
	public static final int paddingTop = 16;
	public static final int paddingRight = 16;
	public static final int paddingBottom = 16;

	// index.json/channelName?searchDetail
	// index.json/all?search=%23%E4%B8%8A%E9%99%90%EF%BC%9A0%20~%20100
	// index.json/chem?search=%23%E7%B2%BE%E5%93%81
	public static final String conversationListURL = "https://chaoli.club/conversations/index.json";
	// index.json/1430/p2
	public static final String postListURL = "https://chaoli.club/conversation/index.json";
	public static final String loginURL = "https://chaoli.club/index.php/user/login";
	public static final String replyURL = "https://chaoli.club/index.php/?p=conversation/reply.ajax";
	public static final String editURL = "https://chaoli.club/index.php/?p=conversation/editPost.ajax";
	public static final String notifyNewMsgURL = "https://chaoli.club/index.php/settings/notificationCheck";
	public static final String avatarURL = "https://dn-chaoli-upload.qbox.me";

	public static final String conversationSP = "conversationList";
	public static final String conversationSPKey = "listJSON";

	public static final String postSP = "postList";
	public static final String postSPKey = "listJSON";

	public static final String loginSP = "loginReturn";
	public static final String loginSPKey = "listJSON";
	public static final String loginBool = "logged";

	public static final int FINISH_CONVERSATION_LIST_ANALYSIS = 1;
	public static final int FINISH_POST_LIST_ANALYSIS = 2;
	public static final int FINISH_LOGIN_LIST_ANALYSIS = 3;
	public static final int FINISH_LOGIN = 4;
}
