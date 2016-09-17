package com.geno.chaoli.forum.meta;

public class Constants
{
	public static final int paddingLeft = 16;
	public static final int paddingTop = 16;
	public static final int paddingRight = 16;
	public static final int paddingBottom = 16;
	public static final int getNotificationInterval = 15;

	public static final String BASE_BASE_URL = "https://www.chaoli.club/";
	public static final String BASE_URL = BASE_BASE_URL + "index.php/";
	public static final String LOGIN_URL = BASE_URL + "user/login?return=%2F";
	public static final String HOMEPAGE_URL = BASE_URL;
	public static final String LOGOUT_PRE_URL = BASE_URL + "user/logout?token=";
	public static final String GET_CAPTCHA_URL = BASE_URL + "mscaptcha";
	public static final String SIGN_UP_URL = BASE_URL + "user/join?invite=";
	public static final String GET_ALL_NOTIFICATIONS_URL = BASE_URL + "settings/notifications.json";
	// activity.json/32/2
	public static final String GET_ACTIVITIES_URL = BASE_URL + "member/activity.json/";
	// statistics.ajax/32
	public static final String GET_STATISTICS_URL = BASE_URL + "member/statistics.ajax/";
	// index.json/channelName?searchDetail
	// index.json/all?search=%23%E4%B8%8A%E9%99%90%EF%BC%9A0%20~%20100
	// index.json/chem?search=%23%E7%B2%BE%E5%93%81
	//public static final String conversationListURL = BASE_URL + "/conversations/index.json/";
	public static final String conversationListURL = BASE_URL + "conversations/index.json/";
	public static final String ATTACHMENT_IMAGE_URL = "https://dn-chaoli-upload.qbox.me/";
	// index.json/1430/p2
	public static final String postListURL = BASE_URL + "conversation/index.json/";
	public static final String loginURL = BASE_URL + "user/login";
	public static final String replyURL = BASE_URL + "?p=conversation/reply.ajax/";
	public static final String editURL = BASE_URL + "?p=conversation/editPost.ajax/";
	public static final String cancelEditURL = BASE_URL + "?p=attachment/removeSession/";
	public static final String notifyNewMsgURL = BASE_URL + "settings/notificationCheck/";
	public static final String avatarURL = "https://dn-chaoli-upload.qbox.me/";
	// .ajax/<conversationId>/<floor>&userId=<myId>&token=<token>
	public static final String preQuoteURL = BASE_URL + "?p=conversation/quotePost.json/";
	// .json/<postId>&userId=<myId>&token=<token>
	public static final String quoteURL = BASE_URL + "?p=conversation/reply.ajax/";
	public static final String deleteURL = BASE_URL + "?p=conversation/deletePost.ajax/";
	public static final String restoreURL = BASE_URL + "conversation/restorePost/";

	public static final String GET_QUESTION_URL = "https://chaoli.club/reg-exam/get-q.php?tags=";
	public static final String CONFIRM_ANSWER_URL = "https://chaoli.club/reg-exam/confirm.php";

	public static final String GET_PROFILE_URL = BASE_URL + "settings/general.json";
	public static final String CHECK_NOTIFICATION_URL = BASE_URL + "?p=settings/notificationCheck.ajax";
	public static final String UPDATE_URL = BASE_URL + "?p=conversations/update.ajax/all/";
	public static final String MODIFY_SETTINGS_URL = BASE_URL + "settings/general";

	public static final String GO_TO_POST_URL = BASE_URL + "conversation/post/";

	/* 给主题设置版块 */
	public static final String SET_CHANNEL_URL = BASE_URL + "?p=conversation/save.json/";
	/* 发表主题 */
	public static final String POST_CONVERSATION_URL = BASE_URL + "?p=conversation/start.ajax";
	/* 添加可见用户 */
	public static final String ADD_MEMBER_URL = BASE_URL + "?p=conversation/addMember.ajax/";
	/* 取消可见用户 */
	public static final String REMOVE_MEMBER_URL = BASE_URL + "?p=conversation/removeMember.ajax/";
	/* 获取可见用户列表 */
	public static final String GET_MEMBERS_ALLOWED_URL = BASE_URL + "";
	/* 隐藏主题 */
	public static final String IGNORE_CONVERSATION_URL = BASE_URL + "?p=conversation/ignore.ajax/";
	/* 关注主题 */
	public static final String STAR_CONVERSATION_URL = BASE_URL + "?p=conversation/star.json/";

	public static final String conversationSP = "conversationList";
	public static final String conversationSPKey = "listJSON";

	public static final String postSP = "postList";
	public static final String postSPKey = "listJSON";

	public static final String loginSP = "loginReturn";
	public static final String loginSPKey = "listJSON";
	public static final String loginBool = "logged";

	public static final String NONE = "none";
}
