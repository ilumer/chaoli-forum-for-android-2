package com.daquexian.chaoli.forum.network;

import com.daquexian.chaoli.forum.model.HistoryResult;
import com.daquexian.chaoli.forum.model.NotificationList;
import com.daquexian.chaoli.forum.model.Question;
import com.daquexian.chaoli.forum.model.TokenResult;
import com.daquexian.chaoli.forum.model.User;
import com.daquexian.chaoli.forum.model.ConversationListResult;
import com.daquexian.chaoli.forum.model.PostListResult;
import com.google.common.annotations.GwtCompatible;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jianhao on 16-8-25.
 */
public interface ChaoliService {
    @GET("index.php/conversation/index.json/{conversationId}/p{page}")
    Call<PostListResult> listPosts(@Path("conversationId") int conversationId, @Path("page") int page);
    @GET("index.php/conversations/index.json/{channel}")
    Observable<ConversationListResult> listConversations(@Path("channel") String channel, @Query("search") String search);
    @GET("index.php/user/login.json")
    Call<TokenResult> getToken();
    @POST("index.php/user/login?return=%2F")
    Observable<String> login(@Field("username") String username, @Field("password") String password, @Field("token") String token);
    @GET("index.php/settings/general.json")
    Call<User> getProfile();
    @POST("index.php/?p=settings/notificationCheck.ajax")
    Call<NotificationList> checkNotification();
    @GET("index.php/member/activity.json/{userId}/{page}")
    Call<HistoryResult> getHistory(@Path("userId") int userId, @Path("page")int page);
    @GET("reg-exam/get-q.php")
    Call<ArrayList<Question>> getQuestion(@Query("tags") String tag);
}
