package com.geno.chaoli.forum.utils;

import android.content.Context;
import android.util.Log;

import com.geno.chaoli.forum.ChaoliApplication;
import com.geno.chaoli.forum.data.Me;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.model.NotificationList;
import com.geno.chaoli.forum.model.User;
import com.geno.chaoli.forum.network.MyOkHttp;
import com.geno.chaoli.forum.network.MyRetrofit;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by daquexian on 16-3-17.
 * 和账户相关的类，包括获取自己的用户信息、检查是否帖子更新、是否有新动态及更改账户设置
 */
public class AccountUtils {
    private static final String TAG = "AccountUtils";

    public static int RETURN_ERROR = -1;
    public static int FILE_DOSENT_EXIST = -2;

    public static void getProfile(final GetProfileObserver observer){
        MyRetrofit.getService().getProfile()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Me.setProfile(ChaoliApplication.getAppContext(), response.body());
                        observer.onGetProfileSuccess();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        observer.onGetProfileFailure();
                    }
                });
        /*CookieUtils.saveCookie(client, context);
        client.get(context, Constants.GET_PROFILE_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                User.setInstanceFromJSONStr(context, response);
                observer.onGetProfileSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                observer.onGetProfileFailure();
            }
        });*/
    }

    public static void checkNotification(final MessageObserver observer){
        MyRetrofit.getService().checkNotification()
                .enqueue(new Callback<NotificationList>() {
                    @Override
                    public void onResponse(Call<NotificationList> call, Response<NotificationList> response) {
                        NotificationList notificationList = response.body();
                        if (notificationList != null) {
                            observer.onCheckNotificationSuccess(notificationList);
                        } else {
                            observer.onCheckNotificationFailure(RETURN_ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationList> call, Throwable t) {
                        observer.onCheckNotificationFailure(RETURN_ERROR);
                    }
                });
    }

    @Deprecated
    public static void hasUpdate(Context context, int[] conversationIdArr, final MessageObserver observer){
        /*CookieUtils.saveCookie(client, context);
        String conversationIds = intJoin(conversationIdArr, ",");
        RequestParams params = new RequestParams();
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        params.put("conversationIds", conversationIds);
        Log.i("conversationIds", conversationIds);
        client.get(context, Constants.UPDATE_URL, params, new AsyncHttpResponseHandler() {
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
        });*/
    }

    public static void modifySettings(File avatar, String language, Boolean privateAdd, Boolean starOnReply, Boolean starPrivate, Boolean hideOnline,
                                      final String signature, String userStatus, final ModifySettingsObserver observer){
        MyOkHttp.MyOkHttpClient myOkHttpClient = new MyOkHttp.MyOkHttpClient()
                .add("token", LoginUtils.getToken())
                .add("language", language)
                .add("userStatus", userStatus)
                .add("signature", signature);
        myOkHttpClient.add("avatar", "image/*", avatar);
        if(hideOnline) myOkHttpClient.add("hideOnline", hideOnline.toString());
        if(starPrivate) myOkHttpClient.add("starPrivate", starPrivate.toString());
        if(starOnReply) myOkHttpClient.add("starOnReply", starOnReply.toString());
        if(privateAdd) myOkHttpClient.add("privateAdd", privateAdd.toString());
        myOkHttpClient.add("save", "保存更改");
        myOkHttpClient
                .post(Constants.MODIFY_SETTINGS_URL)
                .enqueue(ChaoliApplication.getAppContext(), new MyOkHttp.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        observer.onModifySettingsFailure(-3);
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response, String responseStr) throws IOException {
                        observer.onModifySettingsSuccess();
                    }
                });
        /*CookieUtils.saveCookie(client, context);
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
        client.post(context, Constants.MODIFY_SETTINGS_URL, params, new AsyncHttpResponseHandler() {
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
        });*/
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
