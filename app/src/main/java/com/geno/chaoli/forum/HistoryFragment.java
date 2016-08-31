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
import com.geno.chaoli.forum.meta.CookieUtils;
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

public class HistoryFragment extends HomepageListFragment {
    String mUsername, mAvatarSuffix;
    int mUserId;

    public static final String TAG = "HistoryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getArguments().getInt("userId");
        mUsername = getArguments().getString("username");
        mAvatarSuffix = getArguments().getString("avatarSuffix");
    }

    @Override
    public List<? extends ListItem> parseItems(String JSONString) {
        OuterActivity outerActivity = JSON.parseObject(JSONString, OuterActivity.class);
        return outerActivity.activity;
    }

    @Override
    public void bindItemViewHolder(final Context context, MyAdapter adapter, final MyAdapter.MyViewHolder holder, ListItem listItem, int position) {
        final int ITEM_TYPE     = 0;
        final int DIVIDER_TYPE  = 1;
        final int SPACE_TYPE    = 2;

        final String POST_ACTIVITY   = "postActivity";
        final String STATUS         = "status";
        final String JOIN           = "join";

        if(adapter.getItemViewType(position) == ITEM_TYPE) {
            MyActivity thisActivity = (MyActivity) listItem;
            Resources res = getResources();
            holder.avatarView.update(context, mAvatarSuffix, mUserId, mUsername);
            holder.avatarView.scale(20);
            switch (thisActivity.type) {
                case POST_ACTIVITY:
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
                            final ProgressDialog progressDialog = ProgressDialog.show(context, "", getResources().getString(R.string.just_a_sec));
                            AsyncHttpClient client = new AsyncHttpClient();
                            CookieUtils.saveCookie(client, mCallback);
                            client.get(context, "https://chaoli.club/index.php/conversation/post/" + ((TextView) v).getHint(), new AsyncHttpResponseHandler() {
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
                                            String intentToPage = String.valueOf(Integer.parseInt(matcher.group(1)) / 20 + 1);
                                            Log.d(TAG, "page = " +  intentToPage);
                                            intent.putExtra("page", intentToPage);
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
    }

    @Override
    public String getURL() {
        return Constants.GET_ACTIVITIES_URL + mUserId;
    }

    private static class OuterActivity{
        public List<MyActivity> getActivity() {
            return activity;
        }

        public void setActivity(List<MyActivity> activity) {
            this.activity = activity;
        }

        List<MyActivity> activity;
    }
    private static class MyActivity extends ListItem{
        String start;
        String postId;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        String content;
        String title;
        String description;
        String data;

        private static class Data{
            public String getNewStatus() {
                return newStatus;
            }

            public void setNewStatus(String newStatus) {
                this.newStatus = newStatus;
            }

            public String getNewSignature() {
                return newSignature;
            }

            public void setNewSignature(String newSignature) {
                this.newSignature = newSignature;
            }

            String newStatus;
            String newSignature;
        }
    }
}
