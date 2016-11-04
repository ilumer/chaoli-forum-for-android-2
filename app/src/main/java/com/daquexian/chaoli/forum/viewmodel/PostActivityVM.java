package com.daquexian.chaoli.forum.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.daquexian.chaoli.forum.ChaoliApplication;
import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.binding.PostLayoutSelector;
import com.daquexian.chaoli.forum.meta.Constants;
import com.daquexian.chaoli.forum.model.Post;
import com.daquexian.chaoli.forum.model.PostListResult;
import com.daquexian.chaoli.forum.network.MyRetrofit;
import com.daquexian.chaoli.forum.utils.MyUtils;

import java.util.List;

/**
 * Created by jianhao on 16-9-21.
 */

public class PostActivityVM extends BaseViewModel {
    public int conversationId;
    public String title;
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public final ObservableArrayList postList = new ObservableArrayList();
    public int page;
    boolean isAuthorOnly;

    private Boolean reversed = false;

    public ObservableInt listPosition = new ObservableInt();
    public ObservableBoolean showToast = new ObservableBoolean(false);
    public ObservableField<String> toastContent = new ObservableField<>();

    public ObservableBoolean goToReply = new ObservableBoolean(false);
    public ObservableBoolean goToQuote = new ObservableBoolean();
    public ObservableBoolean goToHomepage = new ObservableBoolean(false);
    public Post clickedPost;

    private static final String TAG = "PostActivityVM";

    public PostLayoutSelector layoutSelector = new PostLayoutSelector();

    private void getList(final int page) {
        getList(page, false);
    }

    private void getList(int page, final Boolean refresh) {
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
                        int oldLen = postList.size();
                        List<Post> newPostList = response.body().getPosts();
                        MyUtils.expandUnique(postList, newPostList);
                        listPosition.set(refresh ? 0 : oldLen);
                        listPosition.notifyChange();
                        isRefreshing.set(false);

                        if (!refresh) PostActivityVM.this.page++;
                    }

                    @Override
                    public void onFailure(retrofit2.Call<PostListResult> call, Throwable t) {
                        isRefreshing.set(false);
                        toastContent.set(ChaoliApplication.getAppContext().getString(R.string.network_err));
                        showToast.notifyChange();
                        t.printStackTrace();
                    }
                });
    }

    public void refresh() {
        page = 1;
        getList(0, true);
    }

    public void loadMore() {
        getList(postList.size() < page * Constants.POST_PER_PAGE ? page : (page + 1));
    }

    public void clickFab() {
        goToReply.notifyChange();
    }

    public void quote(Post post) {
        clickedPost = post;
        goToQuote.notifyChange();
    }

    public void replyComplete() {
        isRefreshing.set(true);
        loadMore();
    }

    public void clickAvatar(Post post) {
        clickedPost = post;
        goToHomepage.notifyChange();
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
