package com.geno.chaoli.forum.meta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.alibaba.fastjson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jianhao on 16-3-17.
 */
public class AccountUtils {
    static AsyncHttpClient client = new AsyncHttpClient();
    public static String CHECK_NOTIFICATION_URL = "https://chaoli.club/index.php/?p=settings/notificationCheck.ajax";
    public static String UPDATE_URL = "https://chaoli.club/index.php/?p=conversations/update.ajax/all/";
    public static String MODIFY_SETTINGS_URL = "https://chaoli.club/index.php/settings/general";

    public static int RETURN_ERROR = -1;
    public static int FILE_DOSENT_EXIST = -2;

    public static void checkNotification(Context context, final AccountObserver observer){
        CookieUtils.saveCookie(client, context);
        RequestParams params = new RequestParams();
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        client.get(context, CHECK_NOTIFICATION_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("success", new String(responseBody));
                String response = new String(responseBody);
                String messageFormat = "\"count\":(\\d+)";
                Pattern pattern = Pattern.compile(messageFormat);
                Matcher matcher = pattern.matcher(response);
                if(matcher.find()){
                    observer.onCheckNotificationSuccess(Integer.valueOf(matcher.group(1)));
                }else{
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

    public static void hasUpdate(Context context, int[] conversationIdArr, final AccountObserver observer){
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
                                      final String signature, String userStatus, final AccountObserver observer){
        CookieUtils.saveCookie(client, context);
        client.setTimeout(60000);
        Log.i("begin", "b");
        RequestParams params = new RequestParams();
        params.put("token", LoginUtils.getToken());
        try {
            params.put("avatar", avatar);
        } catch (FileNotFoundException e){
            Log.e("don't exist", "don't exist");
            observer.onModifySettingsFailure(FILE_DOSENT_EXIST);
            return;
        }
        params.put("language", language);
        params.put("userStatus", userStatus);
        params.put("signature", signature);
        params.put("hideOnline", hideOnline.toString());
        params.put("starPrivate", starPrivate.toString());
        params.put("starOnReply", starOnReply.toString());
        params.put("privateAdd", privateAdd.toString());
        params.put("save", "保存更改");
        client.post(context, MODIFY_SETTINGS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("wrong", "wrong");
                observer.onGetUpdateFailure(RETURN_ERROR);
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

    public interface AccountObserver{
        void onGetUpdateSuccess(Boolean hasUpdate);
        void onGetUpdateFailure(int statusCode);
        void onModifySettingsSuccess();
        void onModifySettingsFailure(int statusCode);
        void onCheckNotificationSuccess(int noti_num);
        void onCheckNotificationFailure(int statusCode);
    }
}
