package com.geno.chaoli.forum.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geno.chaoli.forum.ChaoliApplication;
import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.binding.PostLayoutSelector;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.model.Conversation;
import com.geno.chaoli.forum.model.Post;
import com.geno.chaoli.forum.model.PostListResult;
import com.geno.chaoli.forum.network.MyRetrofit;
import com.geno.chaoli.forum.utils.ConversationUtils;
import com.geno.chaoli.forum.view.HomepageActivity;
import com.geno.chaoli.forum.view.IView;
import com.geno.chaoli.forum.view.PostActivity;
import com.geno.chaoli.forum.view.ReplyAction;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.geno.chaoli.forum.R.id.postList;
import static com.geno.chaoli.forum.R.id.swipyRefreshLayout;

/**
 * Created by jianhao on 16-9-21.
 */

public class PostActivityViewModel extends BaseViewModel {
    public int conversationId;
    public String title;
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public final ObservableArrayList postList = new ObservableArrayList();
    public int page;
    boolean isAuthorOnly;

    public ObservableBoolean showToast = new ObservableBoolean(false);
    public ObservableField<String> toastContent = new ObservableField<>();

    public ObservableBoolean goToReply = new ObservableBoolean(false);
    public ObservableBoolean goToHomepage = new ObservableBoolean(false);
    public ObservableField<Post> clickedPost = new ObservableField<>();

    public static final int REPLY_CODE = 1;

    private static final String TAG = "PostActivityVM";

    public PostLayoutSelector layoutSelector = new PostLayoutSelector();

    public void getList(final int page) {
        isRefreshing.set(true);
        MyRetrofit.getService()
                .listPosts(conversationId, page)
                .enqueue(new retrofit2.Callback<PostListResult>() {
                    @Override
                    public void onResponse(retrofit2.Call<PostListResult> call, retrofit2.Response<PostListResult> response) {
                        /*List<Post> newPostList = response.body().getPosts();
                        List<Post> postList = mPostListAdapter.getPosts();
                        int oldLen = postList.size();
                        expandUnique(postList, newPostList);
                        mPostListAdapter.setPosts(postList);
                        mPostListAdapter.notifyItemRangeInserted(oldLen, postList.size() - oldLen);

                        //postListRv.smoothScrollToPosition(page * POST_NUM_PER_PAGE + 1);
                        swipyRefreshLayout.setRefreshing(false);
                        page = (postList.size() + POST_NUM_PER_PAGE - 1) / POST_NUM_PER_PAGE;
                        postListRv.smoothScrollToPosition(page == 1 ? 0 : oldLen);*/
                        List<Post> newPostList = response.body().getPosts();
                        expandUnique(postList, newPostList);
                        isRefreshing.set(false);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<PostListResult> call, Throwable t) {
                        isRefreshing.set(false);
                        toastContent.set(ChaoliApplication.getAppContext().getString(R.string.network_err));
                        showToast.set(true);
                        t.printStackTrace();
                    }
                });
    }

    public void loadMore() {
        getList(postList.size() < page * Constants.POST_PER_PAGE ? page : page + 1);
    }

    public void clickFab() {
        goToReply.set(true);
        goToReply.set(false);
    }

    public void replyComplete(int requestCode, int resultCode, Intent data) {
        if (requestCode == REPLY_CODE) {
            if (resultCode == RESULT_OK) {
                isRefreshing.set(true);
                loadMore();
            }
        }
    }

    public void clickAvatar(Post post) {
        clickedPost.set(post);
        goToHomepage.set(true);
        goToHomepage.set(false);
    }

    /**
     * 针对帖子读取到最后的情况，只往帖子列表中增加多出来的帖子
     *
     * @param A 已有的帖子列表
     * @param B 获取到的一页帖子列表
     * @return 新帖子列表的长度
     */
    private int expandUnique(List<Post> A, List<Post> B) {
        int lenA = A.size();
        if (lenA == 0) {
            A.addAll(B);
        } else {
            int i;
            for (i = 0; i < B.size(); i++)
                if (B.get(i).getTime() > A.get(lenA - 1).getTime())
                    break;
            A.addAll(B.subList(i, B.size()));
        }
        return A.size();
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAuthorOnly() {
        return isAuthorOnly;
    }

    public void setAuthorOnly(boolean authorOnly) {
        isAuthorOnly = authorOnly;
    }

}
