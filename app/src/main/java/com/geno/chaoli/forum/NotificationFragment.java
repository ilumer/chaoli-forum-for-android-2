package com.geno.chaoli.forum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jianhao on 16-6-5.
 */

public class NotificationFragment extends HomepageListFragment {
    public static final String TAG = "NotificationFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        super.setDirection(SwipyRefreshLayoutDirection.TOP);
        return view;
    }

    @Override
    public List<? extends ListItem> parseItems(String JSONString) {
        OuterObject outerObject = JSON.parseObject(JSONString, OuterObject.class);
        return outerObject.results;
    }

    @Override
    public void bindItemViewHolder(final Context context, MyAdapter adapter, final MyAdapter.MyViewHolder holder, ListItem listItem, int position) {
        final int ITEM_TYPE     = 0;
        final int DIVIDER_TYPE  = 1;
        final int SPACE_TYPE    = 2;

        final String TYPE_MENTION        = "mention";
        final String TYPE_POST           = "post";
        final String TYPE_PRIVATE_ADD    = "privateAdd";

        if(adapter.getItemViewType(position) == ITEM_TYPE) {
            final NotificationItem thisItem = (NotificationItem) listItem;
            Resources res = getResources();
            Log.d(TAG, thisItem.avatarSuffix + thisItem.fromMemberId);
            holder.avatarView.update(context, thisItem.avatarSuffix, Integer.parseInt(thisItem.fromMemberId), thisItem.fromMemberName);
            holder.avatarView.scale(20);

            holder.content_tv.setText(thisItem.data.title);
            holder.description_tv.setText(getString(R.string.mention_you, thisItem.fromMemberName));
            holder.content_tv.setHint(thisItem.data.postId);
            holder.description_tv.setHint(thisItem.data.conversationId);
            switch (thisItem.getType()){
                case TYPE_MENTION:
                    holder.description_tv.setText(getString(R.string.mention_you, thisItem.fromMemberName));
                    break;
                case TYPE_POST:
                    holder.description_tv.setText(getString(R.string.someone_update, thisItem.fromMemberName));
                    break;
                case TYPE_PRIVATE_ADD:
                    holder.description_tv.setText(getString(R.string.send_you_a_private_post, thisItem.fromMemberName));
                    break;
            }

            View.OnClickListener onClickListener = null;
            switch (thisItem.getType()){
                case TYPE_POST:
                case TYPE_MENTION:
                    onClickListener = new View.OnClickListener() {
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
                    break;
                case TYPE_PRIVATE_ADD:
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mCallback, PostActivity.class);
                            Log.d(TAG, thisItem.data.conversationId);
                            intent.putExtra("conversationId", Integer.valueOf(thisItem.data.conversationId));
                            intent.putExtra("title", thisItem.data.title);
                            startActivity(intent);
                        }
                    };
                    break;
            }
            holder.content_tv.setOnClickListener(onClickListener);

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
        return Constants.GET_ALL_NOTIFICATIONS_URL;
    }

    private static class OuterObject{
        public List<NotificationItem> getResults() {
            return results;
        }

        public void setResults(List<NotificationItem> results) {
            this.results = results;
        }

        List<NotificationItem> results;
    }

    private static class NotificationItem extends ListItem{
        String fromMemberId;
        String fromMemberName;

        public String getFromMemberId() {
            return fromMemberId;
        }

        public void setFromMemberId(String fromMemberId) {
            this.fromMemberId = fromMemberId;
        }

        public String getFromMemberName() {
            return fromMemberName;
        }

        public void setFromMemberName(String fromMemberName) {
            this.fromMemberName = fromMemberName;
        }

        public Boolean getUnread() {
            return unread;
        }

        public void setUnread(Boolean unread) {
            this.unread = unread;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAvatarSuffix() {
            return avatarSuffix;
        }

        public void setAvatarSuffix(String avatarSuffix) {
            this.avatarSuffix = avatarSuffix;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        Boolean unread;
        String content;
        @JSONField(name = "avatarFormat")
        String avatarSuffix;
        Data data;

        private static class Data{
            String conversationId;
            String postId;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getConversationId() {
                return conversationId;
            }

            public void setConversationId(String conversationId) {
                this.conversationId = conversationId;
            }

            public String getPostId() {
                return postId;
            }

            public void setPostId(String postId) {
                this.postId = postId;
            }

            String title;
        }
    }
}
