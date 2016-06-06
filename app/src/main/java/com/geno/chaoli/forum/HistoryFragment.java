package com.geno.chaoli.forum;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jianhao on 16-6-5.
 */
public class HistoryFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout mSwipyRefreshLayout;
    RecyclerView rvHistory;
    MyAdpter myAdpter;
    Context mCallback;
    String startTime = String.valueOf(Long.MIN_VALUE);  //第一条记录的时间
    String endTime = String.valueOf(Long.MAX_VALUE);    //最后一条记录的时间
    int mUserId;
    String mUsername;
    String mAvatarSuffix;
    int mPage = 1;
    Boolean bottom = true;                                     //判断RecyclerView是否滑动到底部

    final static String TAG = "HistoryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getArguments().getInt("userId");
        mUsername = getArguments().getString("username");
        mAvatarSuffix = getArguments().getString("avatarSuffix");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = context;
        myAdpter = new MyAdpter(mCallback, new ArrayList<MyActivity>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage_history, container, false);

        mSwipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.srl_activities);
        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mSwipyRefreshLayout.setOnRefreshListener(this);
        mSwipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipyRefreshLayout.setRefreshing(true);
                onRefresh(SwipyRefreshLayoutDirection.TOP); // why is it also needed?
            }
        });

        rvHistory = (RecyclerView) view.findViewById(R.id.rvHomepageItems);
        rvHistory.setLayoutManager(new LinearLayoutManager(mCallback));
        rvHistory.setAdapter(myAdpter);

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
        return view;
    }
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        final SwipyRefreshLayout swipyRefreshLayout = mSwipyRefreshLayout;
        if(direction == SwipyRefreshLayoutDirection.TOP){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mCallback, Constants.GET_ACTIVITIES_URL + mUserId, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String response = new String(responseBody);

                    OuterActivity outerActivity = JSON.parseObject(response, OuterActivity.class);

                    int i = 0;
                    for(; i < outerActivity.activity.size(); i++){
                        if(Long.parseLong(startTime) >= Long.parseLong(outerActivity.activity.get(i).time)){
                            break;
                        }
                    }

                    i--;

                    for (; i >= 0; i--) {
                        MyActivity thisActivity = outerActivity.activity.get(i);
                        myAdpter.myActivities.add(0, thisActivity);
                        /*LinearLayout linearLayout = inflateItem(thisActivity);
                        activities_ll.addView(linearLayout, 0);
                        //activities_ll.addView();*/
                    }

                    startTime = outerActivity.activity.get(0).time;

                    swipyRefreshLayout.setRefreshing(false);

                    addTimeDivider(myAdpter.myActivities);
                    myAdpter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    swipyRefreshLayout.setRefreshing(false);
                }
            });
        }else{
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mCallback, Constants.GET_ACTIVITIES_URL + mUserId + "/" + (mPage + 1), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Resources res = getResources();
                    String response = new String(responseBody);

                    OuterActivity outerActivity = JSON.parseObject(response, OuterActivity.class);

                    if(outerActivity.activity.size() == 0){
                        swipyRefreshLayout.setRefreshing(false);
                        return;
                    }

                    int i = 0;
                    for(; i < outerActivity.activity.size(); i++){
                        if(Long.parseLong(endTime) >= Long.parseLong(outerActivity.activity.get(i).time)){
                            break;
                        }
                    }

                    for (; i < outerActivity.activity.size(); i++) {
                        MyActivity thisActivity = outerActivity.activity.get(i);
                        myAdpter.myActivities.add(thisActivity);
                        //activities_ll.addView();
                    }

                    endTime = outerActivity.activity.get(i - 1).time;
                    mPage++;

                    swipyRefreshLayout.setRefreshing(false);

                    addTimeDivider(myAdpter.myActivities);
                    myAdpter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    swipyRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public void setRefreshEnabled(Boolean enabled){
        mSwipyRefreshLayout.setEnabled(enabled || bottom);
    }

    private static class OuterActivity{
        List<MyActivity> activity;
    }

    private static class MyActivity{
        String type;
        String start;
        String postId;
        String content;
        String title;
        String description;
        String data;
        String time;

        private static class Data{
            String newStatus;
            String newSignature;
        }

        MyActivity(){

        }
        MyActivity(String type, int date){
            this.type = type;
            this.time = String.valueOf(date);
        }
    }

    private void addTimeDivider(List<MyActivity> myActivities){
        final String DIVIDER = "divider";
        int dateNow;
        if(myActivities.size() > 0 && !DIVIDER.equals(myActivities.get(0).type)){
            int firstDate = (int) ((Long.parseLong(myActivities.get(0).time) + 8 * 60 * 60) / 24 / 60 / 60);
            dateNow = (int) (Calendar.getInstance().getTimeInMillis() / 1000 / 24 / 60 / 60);
            myActivities.add(0, new MyActivity(DIVIDER, dateNow - firstDate));
        }
        for(int i = 0; i < myActivities.size() - 1; i++){
            MyActivity thisActivity = myActivities.get(i), nextActivity = myActivities.get(i + 1);
            if(!DIVIDER.equals(thisActivity.type) && !DIVIDER.equals(nextActivity.type)){
                int thisDate = (int) ((Long.parseLong(thisActivity.time) + 8 * 60 * 60) / 24 / 60 / 60);
                int nextDate = (int) ((Long.parseLong(nextActivity.time) + 8 * 60 * 60) / 24 / 60 / 60);
                if(thisDate != nextDate) {
                    dateNow = (int) (Calendar.getInstance().getTimeInMillis() / 1000 / 24 / 60 / 60);
                    myActivities.add(i + 1, new MyActivity(DIVIDER, dateNow - nextDate));
                }
            }
        }
    }

    class MyAdpter extends RecyclerView.Adapter<MyAdpter.MyViewHolder>{
        Context mContext;
        List<MyActivity> myActivities;

        final int ITEM_TYPE     = 0;
        final int DIVIDER_TYPE  = 1;

        final String POSTACTIVITY   = "postActivity";
        final String STATUS         = "status";
        final String JOIN           = "join";

        MyAdpter(Context context, List<MyActivity> myActivities){
            mContext = context;
            this.myActivities = myActivities;
        }

        @Override
        public MyAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mCallback).
                    inflate(viewType == ITEM_TYPE ? R.layout.history_item : R.layout.history_divider, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            MyActivity thisActivity = myActivities.get(position);
            if(getItemViewType(position) == ITEM_TYPE) {
                Resources res = getResources();
                holder.avatarView.update(mContext, mAvatarSuffix, mUserId, mUsername);
                holder.avatarView.scale(20);
                switch (thisActivity.type) {
                    case POSTACTIVITY:
                        holder.content_tv.setHint(thisActivity.postId);
                        if ("1".equals(thisActivity.start)) {
                            holder.description_tv.setText(R.string.opened_a_conversation);
                            holder.content_tv.setText(thisActivity.title);
                        } else {
                            holder.description_tv.setText(res.getString(R.string.updated, thisActivity.title));
                            holder.content_tv.setText(thisActivity.content);
                        }
                        holder.description_tv.setHint(thisActivity.postId);
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.just_a_sec));
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.get(mContext, "https://chaoli.club/index.php/conversation/post/" + ((TextView) v).getHint(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        String response = new String(responseBody);
                                        Intent intent = new Intent(mCallback, PostActivity.class);

                                        Pattern pattern = Pattern.compile("\"conversationId\":(\\d+)");
                                        Matcher matcher = pattern.matcher(response);
                                        if (matcher.find()) {
                                            int conversationId = Integer.parseInt(matcher.group(1));
                                            intent.putExtra("conversationId", conversationId);
                                        }

                                        pattern = Pattern.compile("<h1 id='conversationTitle'>(.*?)</h1>");
                                        matcher = pattern.matcher(response);
                                        if (matcher.find()) {
                                            String title = matcher.group(1);
                                            intent.putExtra("title", title);
                                        }

                                        if (v.equals(holder.content_tv)) {
                                            pattern = Pattern.compile("\"startFrom\":(\\d+)");
                                            matcher = pattern.matcher(response);
                                            if (matcher.find()) {
                                                String intentToPage = "/p" + String.valueOf(Integer.parseInt(matcher.group(1)) / 20 + 1);
                                                intent.putExtra("intentToPage", intentToPage);
                                            }
                                        }
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        };
                        holder.content_tv.setOnClickListener(onClickListener);
                        break;
                    case STATUS:
                        holder.description_tv.setText(R.string.modified_his_or_her_information);
                        MyActivity.Data data = JSON.parseObject(thisActivity.data, MyActivity.Data.class);
                        holder.content_tv.setText("");
                        if (data != null && data.newStatus != null) {
                            holder.content_tv.setText(data.newStatus);
                        }
                        break;
                    case JOIN:
                        holder.description_tv.setText(R.string.join_the_forum);
                        holder.content_tv.setText("");
                        break;
                }
                TextPaint tp = holder.description_tv.getPaint();
                tp.setFakeBoldText(true);
            }else if(getItemViewType(position) == DIVIDER_TYPE){
                int timeDiff = Integer.parseInt(thisActivity.time);
                if(timeDiff == 0){
                    holder.time_tv.setText(R.string.today);
                } else {
                    holder.time_tv.setText(getString(R.string.days_ago, timeDiff));
                }
                /*if(timeDiff == 0){
                    holder.time_tv.setText(R.string.today);
                } else if(timeDiff < 7) {
                    holder.time_tv.setText(getString(R.string.days_ago, timeDiff));
                }else if(timeDiff <= 28){
                    holder.time_tv.setText(getString(R.string.weeks_ago, timeDiff / 7));
                }else if(timeDiff <= 45){
                    holder.time_tv.setText(getString(R.string.months_ago, timeDiff / 30));
                }*/
            }
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return myActivities.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView content_tv, description_tv, time_tv;
            AvatarView avatarView;
            MyViewHolder(View view){
                super(view);
                // For item
                content_tv = (TextView) view.findViewById(R.id.tv_post_content);
                description_tv = (TextView) view.findViewById(R.id.tv_description);
                avatarView = (AvatarView) view.findViewById(R.id.avatar);
                // For divider
                time_tv = (TextView) view.findViewById(R.id.tvTime);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return "divider".equals(myActivities.get(position).type) ? DIVIDER_TYPE : ITEM_TYPE;
        }
    }

    public Boolean isBottom(){
        return bottom;
    }
}
