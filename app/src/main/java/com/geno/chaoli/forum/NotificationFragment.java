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

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.model.NotificationItem;
import com.geno.chaoli.forum.network.MyOkHttp;
import com.geno.chaoli.forum.network.MyOkHttp.Callback;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 在个人主页页面显示通知的Fragment
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
        OuterObject outerObject = new Gson().fromJson(JSONString, OuterObject.class);
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
            holder.avatarView.update(context, thisItem.getAvatarSuffix(), Integer.parseInt(thisItem.getFromMemberId()), thisItem.getFromMemberName());
            holder.avatarView.scale(20);

            holder.content_tv.setText(thisItem.getData().getTitle());
            holder.description_tv.setText(getString(R.string.mention_you, thisItem.getFromMemberName()));
            holder.content_tv.setHint(thisItem.getData().getPostId());
            holder.description_tv.setHint(thisItem.getData().getConversationId());
            switch (thisItem.getType()){
                case TYPE_MENTION:
                    holder.description_tv.setText(getString(R.string.mention_you, thisItem.getFromMemberName()));
                    break;
                case TYPE_POST:
                    holder.description_tv.setText(getString(R.string.someone_update, thisItem.getFromMemberName()));
                    break;
                case TYPE_PRIVATE_ADD:
                    holder.description_tv.setText(getString(R.string.send_you_a_private_post, thisItem.getFromMemberName()));
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
                            new MyOkHttp.MyOkHttpClient()
                                    .get(Constants.GO_TO_POST_URL + ((TextView) v).getHint())
                                    .enqueue(context, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response, String responseStr) throws IOException {
                                            Intent intent = new Intent(mCallback, PostActivity.class);

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
                    break;
                case TYPE_PRIVATE_ADD:
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mCallback, PostActivity.class);
                            intent.putExtra("conversationId", Integer.valueOf(thisItem.getData().getConversationId()));
                            intent.putExtra("title", thisItem.getData().getTitle());
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
}
