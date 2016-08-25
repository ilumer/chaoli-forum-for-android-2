package com.geno.chaoli.forum.retrofitservice;

import com.geno.chaoli.forum.model.ConversationListResult;
import com.geno.chaoli.forum.model.PostListResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by jianhao on 16-8-25.
 */
public interface ChaoliService {
    @GET("conversation/index.json/{postId}")
    Call<PostListResult> listPosts(@Path("postId") String postId);
    @GET("conversations/index.json/{channel}")
    Call<ConversationListResult> listConversations(@Path("channel") String channel);
}
