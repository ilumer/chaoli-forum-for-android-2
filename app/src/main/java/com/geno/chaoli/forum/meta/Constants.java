package com.geno.chaoli.forum.meta;

public class Constants
{
	public static final int paddingLeft = 16;
	public static final int paddingTop = 16;
	public static final int paddingRight = 16;
	public static final int paddingBottom = 16;

	// activity.json/32/2
	public static final String GET_ACTIVITIES_URL = "https://chaoli.club/member/activity.json/";
	// index.json/channelName?searchDetail
	// index.json/all?search=%23%E4%B8%8A%E9%99%90%EF%BC%9A0%20~%20100
	// index.json/chem?search=%23%E7%B2%BE%E5%93%81
	public static final String conversationListURL = "https://chaoli.club/conversations/index.json/";
	// index.json/1430/p2
	public static final String postListURL = "https://chaoli.club/conversation/index.json/";
	public static final String loginURL = "https://chaoli.club/index.php/user/login";
	public static final String replyURL = "https://chaoli.club/index.php/?p=conversation/reply.ajax/";
	public static final String editURL = "https://chaoli.club/index.php/?p=conversation/editPost.ajax/";
	public static final String cancelEditURL = "https://chaoli.club/index.php/?p=attachment/removeSession/";
	public static final String notifyNewMsgURL = "https://chaoli.club/index.php/settings/notificationCheck/";
	public static final String avatarURL = "https://dn-chaoli-upload.qbox.me/";
	// .ajax/<conversationId>/<floor>&userId=<myId>&token=<token>
	public static final String preQuoteURL = "https://chaoli.club/index.php/?p=conversation/quotePost.json/";
	// .json/<postId>&userId=<myId>&token=<token>
	public static final String quoteURL = "https://chaoli.club/index.php/?p=conversation/reply.ajax/";
	public static final String deleteURL = "https://chaoli.club/index.php/?p=conversation/deletePost.ajax/";
	public static final String restoreURL = "https://chaoli.club/index.php/conversation/restorePost/";

	public static final String conversationSP = "conversationList";
	public static final String conversationSPKey = "listJSON";

	public static final String postSP = "postList";
	public static final String postSPKey = "listJSON";

	public static final String loginSP = "loginReturn";
	public static final String loginSPKey = "listJSON";
	public static final String loginBool = "logged";
}
