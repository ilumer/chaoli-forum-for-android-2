package com.geno.chaoli.forum.meta;

public class Constants
{
	public static final int paddingLeft = 16;
	public static final int paddingTop = 16;
	public static final int paddingRight = 16;
	public static final int paddingBottom = 16;
	public static final int getNotificationInterval = 15;

	public static final String BASE_URL = "https://www.chaoli.club";
	public static final String LOGIN_URL = BASE_URL + "/index.php/user/login?return=%2F";
	public static final String HOMEPAGE_URL = BASE_URL + "/index.php";
	public static final String LOGOUT_PRE_URL = BASE_URL + "/index.php/user/logout?token=";
	public static final String GET_CAPTCHA_URL = BASE_URL + "/index.php/mscaptcha";
	public static final String SIGN_UP_URL = BASE_URL + "/index.php/user/join?invite=";
	public static final String GET_ALL_NOTIFICATIONS_URL = BASE_URL + "/index.php/settings/notifications.json";
	// activity.json/32/2
	public static final String GET_ACTIVITIES_URL = BASE_URL + "/index.php/member/activity.json/";
	// statistics.ajax/32
	public static final String GET_STATISTICS_URL = BASE_URL + "/index.php/member/statistics.ajax/";
	// index.json/channelName?searchDetail
	// index.json/all?search=%23%E4%B8%8A%E9%99%90%EF%BC%9A0%20~%20100
	// index.json/chem?search=%23%E7%B2%BE%E5%93%81
	//public static final String conversationListURL = BASE_URL + "/conversations/index.json/";
	public static final String conversationListURL = BASE_URL + "/index.php/conversations/index.json/";
	public static final String ATTACHMENT_IMAGE_URL = "https://dn-chaoli-upload.qbox.me/";
	// index.json/1430/p2
	public static final String postListURL = BASE_URL + "/index.php/conversation/index.json/";
	public static final String loginURL = BASE_URL + "/index.php/user/login";
	public static final String replyURL = BASE_URL + "/index.php/?p=conversation/reply.ajax/";
	public static final String editURL = BASE_URL + "/index.php/?p=conversation/editPost.ajax/";
	public static final String cancelEditURL = BASE_URL + "/index.php/?p=attachment/removeSession/";
	public static final String notifyNewMsgURL = BASE_URL + "/index.php/settings/notificationCheck/";
	public static final String avatarURL = "https://dn-chaoli-upload.qbox.me/";
	// .ajax/<conversationId>/<floor>&userId=<myId>&token=<token>
	public static final String preQuoteURL = BASE_URL + "/index.php/?p=conversation/quotePost.json/";
	// .json/<postId>&userId=<myId>&token=<token>
	public static final String quoteURL = BASE_URL + "/index.php/?p=conversation/reply.ajax/";
	public static final String deleteURL = BASE_URL + "/index.php/?p=conversation/deletePost.ajax/";
	public static final String restoreURL = BASE_URL + "/index.php/conversation/restorePost/";

	public static final String GET_PROFILE_URL = BASE_URL + "/index.php/settings/general.json";
	public static final String CHECK_NOTIFICATION_URL = BASE_URL + "/index.php/?p=settings/notificationCheck.ajax";
	public static final String UPDATE_URL = BASE_URL + "/index.php/?p=conversations/update.ajax/all/";
	public static final String MODIFY_SETTINGS_URL = BASE_URL + "/index.php/settings/general";

	public static final String GO_TO_POST_URL = BASE_URL + "/index.php/conversation/post/";

	public static final String conversationSP = "conversationList";
	public static final String conversationSPKey = "listJSON";

	public static final String postSP = "postList";
	public static final String postSPKey = "listJSON";

	public static final String loginSP = "loginReturn";
	public static final String loginSPKey = "listJSON";
	public static final String loginBool = "logged";

	public static final String NONE = "none";
}
