package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.geno.chaoli.forum.Me;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by daquexian on 16-3-17.
 * 和账户相关的类，包括获取自己的用户信息、检查是否帖子更新、是否有新动态及更改账户设置
 */
public class AccountUtils {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static String GET_PROFILE_URL = "https://chaoli.club/index.php/settings/general.json";
    public static String CHECK_NOTIFICATION_URL = "https://chaoli.club/index.php/?p=settings/notificationCheck.ajax";
    public static String UPDATE_URL = "https://chaoli.club/index.php/?p=conversations/update.ajax/all/";
    public static String MODIFY_SETTINGS_URL = "https://chaoli.club/index.php/settings/general";

    public static int RETURN_ERROR = -1;
    public static int FILE_DOSENT_EXIST = -2;

    public static void getProfile(final Context context, final GetProfileObserver observer){
        CookieUtils.saveCookie(client, context);
        client.get(context, GET_PROFILE_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Me.setInstanceFromJSONStr(context, response);
                observer.onGetProfileSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                observer.onGetProfileFailure();
            }
        });
    }

    // TODO: 16-4-8  modify the callback function according to the new API which can show the content of notifications
    public static void checkNotification(Context context, final MessageObserver observer){
        CookieUtils.saveCookie(client, context);
        RequestParams params = new RequestParams();
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        client.get(context, CHECK_NOTIFICATION_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("success", new String(responseBody));
                String response = new String(responseBody);
                try {
                    NotificationList notificationList = JSON.parseObject(response, NotificationList.class);
                    if (notificationList != null) {
                        observer.onCheckNotificationSuccess(notificationList);
                    } else {
                        observer.onCheckNotificationFailure(RETURN_ERROR);
                    }
                } catch (Exception e){
                    observer.onCheckNotificationFailure(RETURN_ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("cn_error", String.valueOf(statusCode), error);
                observer.onCheckNotificationFailure(statusCode);
            }
        });
    }

    public static class NotificationList{
        public int count;
        public List<Notification> results;
    }

    public static class Notification{
        public String fromMemberId;
        public String fromMemberName;
        @JSONField(name="avatarFormat")
        public String avatarSuffix;
        public Data data;
        public String type;

        public static class Data{
            public String conversationId;
            public String postId;
            public String title;
        }
    }

    public static void hasUpdate(Context context, int[] conversationIdArr, final MessageObserver observer){
        CookieUtils.saveCookie(client, context);
        String conversationIds = intJoin(conversationIdArr, ",");
        RequestParams params = new RequestParams();
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        params.put("conversationIds", conversationIds);
        Log.i("conversationIds", conversationIds);
        client.get(context, UPDATE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                //Log.i("addMember", response);
                String messageFormat = "\"messages\":[\\[\\{](.*?)[\\]\\}]";
                Pattern pattern = Pattern.compile(messageFormat);
                Matcher matcher = pattern.matcher(response);
                //Log.i("success", response);
                //Log.i("success", String.valueOf(response.contains("messages")));
                if (matcher.find()) {
                    //Log.i("message", matcher.group(0));
                    //Log.i("message", "message = " + matcher.group(1));
                    Boolean hasUpdate = !("".equals(matcher.group(1)));
                    observer.onGetUpdateSuccess(hasUpdate);
                }else{
                    observer.onGetUpdateFailure(RETURN_ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("failure", String.valueOf(statusCode));
                observer.onGetUpdateFailure(statusCode);
            }
        });
    }

    public static void modifySettings(Context context, File avatar, String language, Boolean privateAdd, Boolean starOnReply, Boolean starPrivate, Boolean hideOnline,
                                      final String signature, String userStatus, final ModifySettingsObserver observer){
        CookieUtils.saveCookie(client, context);
        client.setTimeout(60000);
        Log.i("begin", "b");
        RequestParams params = new RequestParams();
        params.put("token", LoginUtils.getToken());
        try {
            params.put("avatar", avatar);
        } catch (FileNotFoundException e){
            Log.e("don't exist", "don't exist");
            //observer.onModifySettingsFailure(FILE_DOSENT_EXIST);
            //return;
        }
        params.put("language", language);
        params.put("userStatus", userStatus);
        params.put("signature", signature);
        if(hideOnline) params.put("hideOnline", hideOnline.toString());
        if(starPrivate) params.put("starPrivate", starPrivate.toString());
        if(starOnReply) params.put("starOnReply", starOnReply.toString());
        if(privateAdd) params.put("privateAdd", privateAdd.toString());
        params.put("save", "保存更改");
        client.post(context, MODIFY_SETTINGS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("wrong", "wrong");
                observer.onModifySettingsFailure(RETURN_ERROR);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 302){
                    Log.i("modify successfully", "yeah");
                    observer.onModifySettingsSuccess();
                }else{
                    Log.e("error", "wrong code: " + statusCode);
                    observer.onModifySettingsFailure(statusCode);
                }
            }
        });
    }



    /* 生成类似 1,2,3, 格式的字符串 */
    private static String intJoin(int[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i:
             aArr) {
            sbStr.append(i);
            sbStr.append(sSep);
        }
        return sbStr.toString();
    }

    public interface ModifySettingsObserver{
        void onModifySettingsSuccess();
        void onModifySettingsFailure(int statusCode);
    }
    
    public interface MessageObserver {
        void onGetUpdateSuccess(Boolean hasUpdate);
        void onGetUpdateFailure(int statusCode);
        void onCheckNotificationSuccess(NotificationList notificationList);
        void onCheckNotificationFailure(int statusCode);
    }

    public interface GetProfileObserver{
        void onGetProfileSuccess();
        void onGetProfileFailure();
    }
}
