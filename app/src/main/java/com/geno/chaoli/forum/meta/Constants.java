package com.geno.chaoli.forum.meta;

public class Constants
{
	public static final int paddingLeft = 16;
	public static final int paddingTop = 16;
	public static final int paddingRight = 16;
	public static final int paddingBottom = 16;
	public static final int getNotificationInterval = 15;

	public static final String LOGIN_URL = "https://chaoli.org/index.php/user/login?return=%2F";
	public static final String HOMEPAGE_URL = "https://chaoli.org/index.php";
	public static final String LOGOUT_PRE_URL = "https://chaoli.org/index.php/user/logout?token=";
	public static final String GET_CAPTCHA_URL = "https://chaoli.org/index.php/mscaptcha";
	public static final String SIGN_UP_URL = "https://chaoli.org/index.php/user/join?invite=";
	public static final String GET_ALL_NOTIFICATIONS_URL = "https://chaoli.org/index.php/settings/notifications.json";
	// activity.json/32/2
	public static final String GET_ACTIVITIES_URL = "https://chaoli.org/index.php/member/activity.json/";
	// statistics.ajax/32
	public static final String GET_STATISTICS_URL = "https://chaoli.org/index.php/member/statistics.ajax/";
	// index.json/channelName?searchDetail
	// index.json/all?search=%23%E4%B8%8A%E9%99%90%EF%BC%9A0%20~%20100
	// index.json/chem?search=%23%E7%B2%BE%E5%93%81
	//public static final String conversationListURL = "https://chaoli.org/conversations/index.json/";
	public static final String conversationListURL = "https://chaoli.org/index.php/conversations/index.json";
	// index.json/1430/p2
	public static final String postListURL = "https://chaoli.org/index.php/conversation/index.json/";
	public static final String loginURL = "https://chaoli.org/index.php/user/login";
	public static final String replyURL = "https://chaoli.org/index.php/?p=conversation/reply.ajax/";
	public static final String editURL = "https://chaoli.org/index.php/?p=conversation/editPost.ajax/";
	public static final String cancelEditURL = "https://chaoli.org/index.php/?p=attachment/removeSession/";
	public static final String notifyNewMsgURL = "https://chaoli.org/index.php/settings/notificationCheck/";
	public static final String avatarURL = "https://dn-chaoli-upload.qbox.me/";
	// .ajax/<conversationId>/<floor>&userId=<myId>&token=<token>
	public static final String preQuoteURL = "https://chaoli.org/index.php/?p=conversation/quotePost.json/";
	// .json/<postId>&userId=<myId>&token=<token>
	public static final String quoteURL = "https://chaoli.org/index.php/?p=conversation/reply.ajax/";
	public static final String deleteURL = "https://chaoli.org/index.php/?p=conversation/deletePost.ajax/";
	public static final String restoreURL = "https://chaoli.org/index.php/conversation/restorePost/";

	public static final String conversationSP = "conversationList";
	public static final String conversationSPKey = "listJSON";

	public static final String postSP = "postList";
	public static final String postSPKey = "listJSON";

	public static final String loginSP = "loginReturn";
	public static final String loginSPKey = "listJSON";
	public static final String loginBool = "logged";

	public static final String NONE = "none";
}
