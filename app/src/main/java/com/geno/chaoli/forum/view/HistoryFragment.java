package com.geno.chaoli.forum.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geno.chaoli.forum.ChaoliApplication;
import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.data.Me;
import com.geno.chaoli.forum.databinding.HomepageHistoryBinding;
import com.geno.chaoli.forum.viewmodel.BaseViewModel;
import com.geno.chaoli.forum.viewmodel.HistoryFragmentVM;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

/**
 * Created by jianhao on 16-6-5.
 */

public class HistoryFragment extends Fragment implements IView, SwipyRefreshLayout.OnRefreshListener {
    //String mUsername, mAvatarSuffix;
    //int mUserId;
    SwipyRefreshLayout mSwipyRefreshLayout;
    Boolean bottom;
    Context activityContext;

    private static final String TAG = "HistoryFragment";
    private HistoryFragmentVM viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        //mUserId = arguments.getInt("userId");
        //mUsername = arguments.getString("username");
        //mAvatarSuffix = arguments.getString("avatarSuffix");
        if (arguments.getInt("type") == HistoryFragmentVM.TYPE_ACTIVITY) {
            viewModel = new HistoryFragmentVM(arguments.getInt("type"),
                    arguments.getInt("userId"),
                    arguments.getString("username"),
                    arguments.getString("avatarSuffix"));
        } else if (arguments.getInt("type") == HistoryFragmentVM.TYPE_NOTIFICATION) {
            viewModel = new HistoryFragmentVM(arguments.getInt("type"), Me.getMyUserId(), Me.getMyUsername(), Me.getMyAvatarSuffix());
        } else {
            throw new RuntimeException("type can only be 0 or 1");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipyRefreshLayout = (SwipyRefreshLayout) inflater.inflate(R.layout.homepage_history, container, false);

        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mSwipyRefreshLayout.setOnRefreshListener(this);

        setViewModel(viewModel);

        onRefresh(SwipyRefreshLayoutDirection.TOP);

        RecyclerView rvHistory = (RecyclerView) mSwipyRefreshLayout.findViewById(R.id.rvHomepageItems);
        rvHistory.setLayoutManager(new LinearLayoutManager(activityContext));
        //rvHistory.setAdapter(myAdapter);

        rvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount()-1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom =  recyclerView.getBottom()-recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition  = recyclerView.getLayoutManager().getPosition(lastChildView);

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                if(lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount()-1 ){
                    bottom = true;
                    mSwipyRefreshLayout.setEnabled(true);
                }else{
                    bottom = false;
                }
            }
        });

        viewModel.showProgressDialog.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (((ObservableBoolean) observable).get()) {
                    ((HomepageActivity) activityContext).showProcessDialog(ChaoliApplication.getAppContext().getString(R.string.just_a_sec));
                } else {
                    ((HomepageActivity) activityContext).dismissProcessDialog();
                }
            }
        });

        viewModel.goToPost.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Intent intent = new Intent(activityContext, PostActivity.class);
                intent.putExtra("conversationId", viewModel.intendedConversationId.get());
                intent.putExtra("conversationTitle", viewModel.intendedConversationTitle.get());
                if (viewModel.intendedConversationPage.get() != -1) intent.putExtra("page", viewModel.intendedConversationPage.get());
                startActivity(intent);
            }
        });

        return mSwipyRefreshLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /*@Override
    public List<? extends ListItem> parseItems(String JSONString) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new HistoryAdapterFactory()).create();
        HistoryResult historyResult = gson.fromJson(JSONString, HistoryResult.class);
        return historyResult.activity;
    }*/

    /*@Override
    public void bindItemViewHolder(final Context context, MyAdapter adapter, final MyAdapter.MyViewHolder holder, ListItem listItem, int position) {
        final int ITEM_TYPE     = 0;
        final int DIVIDER_TYPE  = 1;
        final int SPACE_TYPE    = 2;

        final String POST_ACTIVITY  = "postActivity";
        final String STATUS         = "status";
        final String JOIN           = "join";
        final String TYPE_MENTION        = "mention";
        final String TYPE_POST           = "post";
        final String TYPE_PRIVATE_ADD    = "privateAdd";

        if(adapter.getItemViewType(position) == ITEM_TYPE) {
            HistoryItem historyItem = (HistoryItem) listItem;
            Resources res = getResources();
            holder.avatarView.update(mAvatarSuffix, mUserId, mUsername);
            holder.avatarView.scale(20);
            switch (historyItem.getType()) {
                case POST_ACTIVITY:
                    /*holder.content_tv.setHint(historyItem.getPostId());
                    if ("1".equals(historyItem.getStart())) {
                        //holder.description_tv.setText(R.string.opened_a_conversation);
                        //holder.content_tv.setText(historyItem.getTitle());
                    } else {
                        //holder.description_tv.setText(res.getString(R.string.updated, historyItem.getTitle()));
                        //holder.content_tv.setText(historyItem.getContent());
                    }
                    //holder.description_tv.setHint(historyItem.getPostId());
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final ProgressDialog progressDialog = ProgressDialog.show(context, "", getResources().getString(R.string.just_a_sec));
                            new MyOkHttp.MyOkHttpClient()
                                    .get(Constants.GO_TO_POST_URL + ((TextView) v).getHint())
                                    .enqueue(context, new Callback() {
                                        @Override
                                        public void onFailure(okhttp3.Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(okhttp3.Call call, Response response, String responseStr) throws IOException {
                                            Intent intent = new Intent(activityContext, PostActivity.class);

                                            Pattern pattern = Pattern.compile("\"conversationId\":(\\d+)");
                                            Matcher matcher = pattern.matcher(responseStr);
                                            if (matcher.find()) {
                                                int conversationId = Integer.parseInt(matcher.group(1));
                                                intent.putExtra("conversationId", conversationId);
                                            }

                                            pattern = Pattern.compile("<h1 id='conversationTitle'>(.*?)</h1>");
                                            matcher = pattern.matcher(responseStr);
                                            if (matcher.find()) {
                                                String title = matcher.group(1);
                                                title = title.replaceAll("(^<(.*?)>)|(<(.*?)>$)", "");
                                                intent.putExtra("title", title);
                                            }

                                            if (v.equals(holder.content_tv)) {
                                                pattern = Pattern.compile("\"startFrom\":(\\d+)");
                                                matcher = pattern.matcher(responseStr);
                                                if (matcher.find()) {
                                                    int intentToPage = Integer.parseInt(matcher.group(1)) / 20 + 1;
                                                    intent.putExtra("page", intentToPage);
                                                }
                                            }
                                            progressDialog.dismiss();
                                            startActivity(intent);

                                        }
                                    });
                        }
                    };
                    holder.content_tv.setOnClickListener(onClickListener);*/
            /*        break;
                case STATUS:
                    //holder.description_tv.setText(R.string.modified_his_or_her_information);
                    //holder.content_tv.setText("");
                    break;
                case JOIN:
                    break;
                case TYPE_MENTION:
                    //holder.description_tv.setText(getString(R.string.mention_you, thisItem.getFromMemberName()));
                    break;
                case TYPE_POST:
                    //holder.description_tv.setText(getString(R.string.someone_update, thisItem.getFromMemberName()));
                    break;
                case TYPE_PRIVATE_ADD:
                    //holder.description_tv.setText(getString(R.string.send_you_a_private_post, thisItem.getFromMemberName()));
                    break;
            }
        }else if(adapter.getItemViewType(position) == DIVIDER_TYPE){
            if(position == 1)
                holder.divider.setVisibility(View.INVISIBLE);
            int timeDiff = Integer.parseInt(listItem.getTime());
            if(timeDiff == 0){
                holder.time_tv.setText(R.string.today);
            } else {
                holder.time_tv.setText(getString(R.string.days_ago, timeDiff));
            }
        }
    }*/

    /*@Override
    public Call<HistoryResult> getCall(int page){
        return MyRetrofit.getService().getHistory(mUserId, page);
    }*/

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            viewModel.refresh();
        } else {
            viewModel.loadMore();
        }
    }

    public void setRefreshEnabled(Boolean enabled){
        mSwipyRefreshLayout.setEnabled(enabled || bottom);
    }

    public void setDirection(SwipyRefreshLayoutDirection direction){
        mSwipyRefreshLayout.setDirection(direction);
    }

    @Override
    public void setViewModel(BaseViewModel viewModel) {
        this.viewModel = (HistoryFragmentVM) viewModel;
        //this.viewModel.setUrl(Constants.GET_ACTIVITIES_URL + mUserId);
        HomepageHistoryBinding binding = DataBindingUtil.bind(mSwipyRefreshLayout);
        binding.setViewModel(this.viewModel);
    }
}
