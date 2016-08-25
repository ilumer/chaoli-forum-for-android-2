package com.geno.chaoli.forum.meta;

public class Constants
{
	public static final int paddingLeft = 16;
	public static final int paddingTop = 16;
	public static final int paddingRight = 16;
	public static final int paddingBottom = 16;
	public static final int getNotificationInterval = 15;

	public static final String BASE_URL = "https://www.chaoli.club";
	public static final String LOGIN_URL = "https://www.chaoli.club/index.php/user/login?return=%2F";
	public static final String HOMEPAGE_URL = "https://www.chaoli.club/index.php";
	public static final String LOGOUT_PRE_URL = "https://www.chaoli.club/index.php/user/logout?token=";
	public static final String GET_CAPTCHA_URL = "https://www.chaoli.club/index.php/mscaptcha";
	public static final String SIGN_UP_URL = "https://www.chaoli.club/index.php/user/join?invite=";
	public static final String GET_ALL_NOTIFICATIONS_URL = "https://www.chaoli.club/index.php/settings/notifications.json";
	// activity.json/32/2
	public static final String GET_ACTIVITIES_URL = "https://www.chaoli.club/index.php/member/activity.json/";
	// statistics.ajax/32
	public static final String GET_STATISTICS_URL = "https://www.chaoli.club/index.php/member/statistics.ajax/";
	// index.json/channelName?searchDetail
	// index.json/all?search=%23%E4%B8%8A%E9%99%90%EF%BC%9A0%20~%20100
	// index.json/chem?search=%23%E7%B2%BE%E5%93%81
	//public static final String conversationListURL = "https://www.chaoli.club/conversations/index.json/";
	public static final String conversationListURL = "https://www.chaoli.club/index.php/conversations/index.json/";
	// index.json/1430/p2
	public static final String postListURL = "https://www.chaoli.club/index.php/conversation/index.json/";
	public static final String loginURL = "https://www.chaoli.club/index.php/user/login";
	public static final String replyURL = "https://www.chaoli.club/index.php/?p=conversation/reply.ajax/";
	public static final String editURL = "https://www.chaoli.club/index.php/?p=conversation/editPost.ajax/";
	public static final String cancelEditURL = "https://www.chaoli.club/index.php/?p=attachment/removeSession/";
	public static final String notifyNewMsgURL = "https://www.chaoli.club/index.php/settings/notificationCheck/";
	public static final String avatarURL = "https://dn-chaoli-upload.qbox.me/";
	// .ajax/<conversationId>/<floor>&userId=<myId>&token=<token>
	public static final String preQuoteURL = "https://www.chaoli.club/index.php/?p=conversation/quotePost.json/";
	// .json/<postId>&userId=<myId>&token=<token>
	public static final String quoteURL = "https://www.chaoli.club/index.php/?p=conversation/reply.ajax/";
	public static final String deleteURL = "https://www.chaoli.club/index.php/?p=conversation/deletePost.ajax/";
	public static final String restoreURL = "https://www.chaoli.club/index.php/conversation/restorePost/";

	public static final String GET_PROFILE_URL = "https://www.chaoli.club/index.php/settings/general.json";
	public static final String CHECK_NOTIFICATION_URL = "https://www.chaoli.club/index.php/?p=settings/notificationCheck.ajax";
	public static final String UPDATE_URL = "https://www.chaoli.club/index.php/?p=conversations/update.ajax/all/";
	public static final String MODIFY_SETTINGS_URL = "https://www.chaoli.club/index.php/settings/general";

	public static final String GO_TO_POST_URL = "https://www.chaoli.club/index.php/conversation/post/";

	public static final String conversationSP = "conversationList";
	public static final String conversationSPKey = "listJSON";

	public static final String postSP = "postList";
	public static final String postSPKey = "listJSON";

	public static final String loginSP = "loginReturn";
	public static final String loginSPKey = "listJSON";
	public static final String loginBool = "logged";

	public static final String NONE = "none";
}
