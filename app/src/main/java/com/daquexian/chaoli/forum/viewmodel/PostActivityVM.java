package com.daquexian.chaoli.forum.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import com.daquexian.chaoli.forum.ChaoliApplication;
import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.binding.PostLayoutSelector;
import com.daquexian.chaoli.forum.meta.Constants;
import com.daquexian.chaoli.forum.model.Conversation;
import com.daquexian.chaoli.forum.model.Post;
import com.daquexian.chaoli.forum.model.PostListResult;
import com.daquexian.chaoli.forum.network.MyRetrofit;
import com.daquexian.chaoli.forum.utils.MyUtils;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.abego.treelayout.internal.util.java.util.ListUtil;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by jianhao on 16-9-21.
 */

public class PostActivityVM extends BaseViewModel {
    public Conversation conversation;
    public int conversationId;
    public String title;
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ObservableField<SwipyRefreshLayoutDirection> direction = new ObservableField<>(SwipyRefreshLayoutDirection.BOTH);
    public final ObservableArrayList<Post> postList = new ObservableArrayList();
    public int page;
    boolean isAuthorOnly;

    private Boolean reversed = false;
    private Boolean preview = true;     //是否是只加载了第一条帖子的状态

    public ObservableInt listPosition = new ObservableInt();
    public ObservableBoolean showToast = new ObservableBoolean(false);
    public ObservableField<String> toastContent = new ObservableField<>();

    public ObservableBoolean goToReply = new ObservableBoolean(false);
    public ObservableBoolean goToQuote = new ObservableBoolean();
    public ObservableBoolean goToHomepage = new ObservableBoolean(false);
    public Post clickedPost;

    private static final String TAG = "PostActivityVM";

    public PostLayoutSelector layoutSelector = new PostLayoutSelector();

    public PostActivityVM(Conversation conversation) {
        this.conversation = conversation;
        postList.add(new Post(Integer.valueOf(conversation.getStartMemberId()), conversation.getStartMember(), conversation.getStartMemberAvatarSuffix(), conversation.getFirstPost(), conversation.getStartTime()));
        conversationId = conversation.getConversationId();
        title = conversation.getTitle();
    }

    public Boolean isReversed() {
        return reversed;
    }

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
                        Log.d(TAG, "onResponse: " + call.request().url().toString());
                        if (preview) {
                            postList.clear();
                            preview = false;
                        }
                        int oldLen = postList.size();
                        List<Post> newPostList = response.body().getPosts();
                        if (!reversed)
                            MyUtils.expandUnique(postList, newPostList);
                        else
                            postList.addAll(MyUtils.reverse(newPostList));
                        listPosition.set(refresh ? 0 : oldLen);
                        listPosition.notifyChange();
                        isRefreshing.set(false);

                        if (!refresh)
                            PostActivityVM.this.page += reversed ? -1 : 1;

                        direction.set(SwipyRefreshLayoutDirection.BOTTOM);
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

    public void reverse() {
        reversed = !reversed;
        refresh();
    }

    public void refresh() {
        direction.set(SwipyRefreshLayoutDirection.BOTH);
        postList.clear();
        page = reversed ? (int) Math.ceil((conversation.getReplies() + 1) / 20.0) : 1;
        getList(page, true);
    }

    public void loadMore() {
        if (reversed && page == 1) {
            Log.d(TAG, "loadMore: ");
            isRefreshing.set(false);
            isRefreshing.notifyChange();
            return;
        }
        int nextPage = page;
        if (reversed) {
            nextPage = page - 1;
        } else
            if (postList.size() >= page * Constants.POST_PER_PAGE)
                nextPage = page + 1;
        getList(nextPage);
        //getList(postList.size() < page * Constants.POST_PER_PAGE ? page : (reversed ? page - 1 : page + 1));
    }

    public void clickFab() {
        goToReply.notifyChange();
    }

    public void quote(Post post) {
        if (!preview) {
            clickedPost = post;
            goToQuote.notifyChange();
        }
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
