package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by jianhao on 16-3-11.
 */
public class ConversationUtils {
    public static final String setChannelURL = "https://chaoli.club/index.php/?p=conversation/save.json/";
    public static final String postConversationURL = "https://chaoli.club/index.php/?p=conversation/start.ajax";
    public static final String addMemberURL = "https://chaoli.club/index.php/?p=conversation/addMember.ajax/";
    public static final String removeMemberURL = "https://chaoli.club/index.php/?p=conversation/removeMember.ajax/";
    public static final String getMembersAllowedURL = "https://chaoli.club/index.php/";

    public static final int RETURN_ERROR = -1;
    public static final int NO_THIS_MEMBER = -2;

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static ArrayList<Integer> memberList = new ArrayList<>();

    public static void setChannel(final Context context, int channel, SetChannelObverser obverser){
        setChannel(context, channel, 0, obverser);
    }

    public static void setChannel(final Context context, int channel, int conversationId, final SetChannelObverser obverser){
        CookieUtils.saveCookie(client, context);
        String url = setChannelURL + String.valueOf(conversationId);
        RequestParams params = new RequestParams();
        params.put("channel", channel);
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        client.post(context, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                if (response.startsWith("{\"allowedSummary\"")) {
                    obverser.onSetChannelSuccess();
                } else {
                    obverser.onSetChannelFailure(RETURN_ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                obverser.onSetChannelFailure(statusCode);
            }
        });
    }

    public static void addMember(final Context context, String member, AddMemberObverser obverser){
        addMember(context, member, 0, obverser);
    }

    public static void addMember(final Context context, String member, int conversationId, final AddMemberObverser obverser){
        CookieUtils.saveCookie(client, context);
        String url = addMemberURL + String.valueOf(conversationId);
        RequestParams params = new RequestParams();
        params.put("member", member);
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        client.post(context, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.i("addMember", response);
                String idFormat = "data-id='(\\d+)'";
                Pattern pattern = Pattern.compile(idFormat);
                Matcher matcher = pattern.matcher(response);
                Log.i("response", String.valueOf(response.length()));
                if (matcher.find()) {
                    Log.i("id", matcher.group(1));
                    int userIdAdded = Integer.parseInt(matcher.group(1));
                    memberList.add(userIdAdded);
                    obverser.onAddMemberSuccess();
                } else {
                    obverser.onAddMemberFailure(RETURN_ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("addMemberError", String.valueOf(statusCode));
            }
        });
    }

    public static void removeMember(final Context context, int userId, RemoveMemberObverser obverser){
        removeMember(context, userId, 0, obverser);
    }

    public static void removeMember(final Context context, final int userId,
                                    int conversationId, final RemoveMemberObverser obverser){
        CookieUtils.saveCookie(client, context);
        String url = removeMemberURL + String.valueOf(conversationId);
        RequestParams params = new RequestParams();
        params.put("member", userId);
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        client.post(context, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.i("removeMember", response);
                if (response.startsWith("{\"allowedSummary\"")) {
                    if (memberList.contains(Integer.valueOf(userId))) {
                        memberList.remove(Integer.valueOf(userId));
                        obverser.onRemoveMemberSuccess();
                    } else {
                        obverser.onRemoveMemberFailure(NO_THIS_MEMBER);
                    }
                } else {
                    obverser.onRemoveMemberFailure(RETURN_ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("removeMemberError", String.valueOf(statusCode));
                obverser.onRemoveMemberFailure(statusCode);
            }
        });
    }

    public static void postConversation(Context context, String title, String content,
                                        final PostConversationObverser obverser){
        CookieUtils.saveCookie(client, context);

        final RequestParams params = new RequestParams();
        params.put("title", title);
        params.put("content", content);
        params.put("userId", LoginUtils.getUserId());
        params.put("token", LoginUtils.getToken());
        client.post(context, postConversationURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("postConversation", new String(responseBody));
                String response = new String(responseBody);
                if(response.startsWith("{\"redirect\"")){
                    String conIdFormat = "/(\\d+)";
                    Pattern pattern = Pattern.compile(conIdFormat);
                    Matcher matcher = pattern.matcher(response);
                    if(matcher.find()){
                        Log.i("conId", matcher.group(1));
                        obverser.onPostConversationSuccess(Integer.parseInt(matcher.group(1)));
                    }
                } else {
                    obverser.onPostConversationFailure(RETURN_ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("postConversationError", String.valueOf(statusCode));
                obverser.onPostConversationFailure(statusCode);
            }
        });
    }

    public static void getMembersAllowed(Context context, int conId, final GetMembersAllowedObverser obverser){
        CookieUtils.saveCookie(client, context);
        final List<Integer> memberList = new ArrayList<>();
        String url = getMembersAllowedURL;
        RequestParams params = new RequestParams();
        params.put("p", "conversation/membersAllowed.ajax/" + conId);
        params.put("token", LoginUtils.getToken());
        params.put("userId", LoginUtils.getUserId());
        client.get(context, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.i("get", response);
                String idFormat = "data-id='(\\d+)'";
                Pattern pattern = Pattern.compile(idFormat);
                Matcher matcher = pattern.matcher(response);
                while(matcher.find()){
                    memberList.add(Integer.valueOf(matcher.group(1)));
                }
                obverser.onGetMembersAllowedSuccess(memberList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                obverser.onGetMembersAllowedFailure(statusCode);
            }
        });
    }

    public interface SetChannelObverser {
        void onSetChannelSuccess();
        void onSetChannelFailure(int statusCode);
    }

    public interface AddMemberObverser {
        void onAddMemberSuccess();
        void onAddMemberFailure(int statusCode);
    }

    public interface RemoveMemberObverser {
        void onRemoveMemberSuccess();
        void onRemoveMemberFailure(int statusCode);
    }

    public interface PostConversationObverser {
        void onPostConversationSuccess(int conversationId);
        void onPostConversationFailure(int statusCode);
    }

    public interface GetMembersAllowedObverser {
        void onGetMembersAllowedSuccess(List<Integer> memberList);
        void onGetMembersAllowedFailure(int statusCode);
    }
}
