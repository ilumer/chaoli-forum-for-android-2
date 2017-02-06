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

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jianhao on 16-9-21.
 */

public class PostActivityVM extends BaseViewModel {
    public Conversation conversation;
    public int conversationId;
    public String title;
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    //public ObservableField<SwipyRefreshLayoutDirection> direction = new ObservableField<>(SwipyRefreshLayoutDirection.BOTH);
    public ObservableBoolean canRefresh = new ObservableBoolean(true);
    public final ObservableArrayList<Post> postList = new ObservableArrayList();
    public int minPage;
    public int maxPage;
    boolean isAuthorOnly;

    private Boolean reversed = false;
    private Boolean preview = true;     //是否是只加载了第一条帖子的状态
    private Boolean canAutoLoad = false;  // 是否可以自动加载

    public ObservableInt listPosition = new ObservableInt();
    public ObservableBoolean showToast = new ObservableBoolean(false);
    public ObservableField<String> toastContent = new ObservableField<>();
    public ObservableBoolean updateToolbar = new ObservableBoolean();

    public ObservableBoolean goToReply = new ObservableBoolean(false);
    public ObservableBoolean goToQuote = new ObservableBoolean();
    public ObservableBoolean goToHomepage = new ObservableBoolean(false);
    public Post clickedPost;

    private static final String TAG = "PostActivityVM";

    public PostLayoutSelector layoutSelector = new PostLayoutSelector();

    public PostActivityVM(int conversationId, String title) {
        this.conversationId = conversationId;
        this.title = title;
        init();
    }

    public PostActivityVM(Conversation conversation) {
        this.conversation = conversation;
        postList.add(new Post(Integer.valueOf(conversation.getStartMemberId()), conversation.getStartMember(), conversation.getStartMemberAvatarSuffix(), conversation.getFirstPost(), conversation.getStartTime()));
        conversationId = conversation.getConversationId();
        title = conversation.getTitle();
        init();
    }

    private void init() {
        //postList.add(new Post());
    }

    public Boolean isReversed() {
        return reversed;
    }

    /*private void getList(final int page) {
        getList(page, false);
    }*/

    private Observable<List<Post>> getList(int page){
        return MyRetrofit.getService()
                .listPosts(conversationId,page)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showCircle();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<PostListResult, List<Post>>() {
                    @Override
                    public List<Post> call(PostListResult postListResult) {
                        if (conversation == null){
                            conversation = postListResult.getConversation();
                            updateToolbar.notifyChange();
                        }
                        removeCircle();
                        List<Post> newPosts = postListResult.getPosts();
                        if (newPosts.size() == Constants.POST_PER_PAGE) canAutoLoad = true;
                        return newPosts;
                    }
                });
    }

    private Action1<Throwable> errorAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            isRefreshing.set(false);
            toastContent.set(ChaoliApplication.getAppContext().getString(R.string.network_err));
            showToast.notifyChange();
        }
    };

    public void tryToLoadFromBottom() {
        if (canAutoLoad) {
            canAutoLoad = false;
            pullFromBottom();
        }
    }

    public void reverse() {
        reversed = !reversed;
        firstLoad();
    }

    public void reverseBtnClick() {
        if (conversation == null) return;
        reverse();
    }

    public boolean hasFooterView() {
        return postList.size() > 0 && postList.get(postList.size() - 1).username == null;
    }

    public void firstLoad() {
        maxPage = minPage = reversed ? (int) Math.ceil((conversation.getReplies() + 1) / 20.0) : 1;
        getList(maxPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Post>>() {
                    @Override
                    public void call(List<Post> posts) {
                        postList.clear();
                        preview = false;
                        if (reversed) postList.addAll(MyUtils.reverse(posts));
                        else postList.addAll(posts);
                    }
                }, errorAction);

    }

    private void moveToPosition(int position) {
        listPosition.set(position);
        listPosition.notifyChange();
    }
    /*public void loadMore() {
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
    }*/

    private void loadAfterward() {
        final int nextPage;
        if (postList.size() >= (maxPage - minPage + 1) * Constants.POST_PER_PAGE) nextPage = maxPage + 1;
        else nextPage = maxPage;

        final int oldLen = postList.size();

        getList(nextPage)
                .subscribe(new Action1<List<Post>>() {
                    @Override
                    public void call(List<Post> posts) {
                        Log.d(TAG, "doWhenSuccess: " + posts.size());
                        if (reversed) {
                            MyUtils.expandUnique(postList, MyUtils.reverse(posts), false, reversed);
                            moveToPosition(0);
                        }
                        else MyUtils.expandUnique(postList, posts);
                        maxPage = nextPage;
                    }
                });
    }

    private void loadBackward() {
        if (minPage == 1) {
            removeCircle();
            return;
        }
        final int nextPage = minPage - 1;
        final int oldLen = postList.size();
        getList(nextPage)
                .subscribe(new Action1<List<Post>>() {
                    @Override
                    public void call(List<Post> posts) {
                        if (reversed) postList.addAll(MyUtils.reverse(posts));
                        else MyUtils.expandUnique(postList, posts, false);
                        minPage = nextPage;
                    }
                }, errorAction);
    }

    /**
     * 去掉刷新时的圆圈
     */
    public void removeCircle() {
        isRefreshing.set(false);
        isRefreshing.notifyChange();
    }

    /**
     * 显示刷新时的圆圈
     */
    public void showCircle() {
        isRefreshing.set(true);
        isRefreshing.notifyChange();
    }

    public void pullFromTop() {
        if (isRefreshing.get()) return;
        if (isReversed()) loadAfterward();
        else loadBackward();
    }

    public void pullFromBottom() {
        if (isRefreshing.get()) return;
        if (isReversed()) loadBackward();
        else loadAfterward();
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
        loadAfterward();
    }

    public void clickAvatar(Post post) {
        if (post.username == null) return;
        clickedPost = post;
        goToHomepage.notifyChange();
    }

    public void setPage(int page) {
        //this.page = page;
        maxPage = minPage = page;
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

    private interface SuccessCallback {
        void doWhenSuccess(List<Post> newPostList);
    }
}
