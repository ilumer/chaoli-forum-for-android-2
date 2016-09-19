package com.geno.chaoli.forum.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.model.HistoryItem;
import com.geno.chaoli.forum.model.HistoryResult;
import com.geno.chaoli.forum.network.MyOkHttp;
import com.geno.chaoli.forum.network.MyOkHttp.Callback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

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
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new HistoryAdapterFactory()).create();
        HistoryResult historyResult = gson.fromJson(JSONString, HistoryResult.class);
        return historyResult.activity;
    }

    private static class HistoryAdapterFactory implements TypeAdapterFactory {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != HistoryItem.Data.class) return null;

            TypeAdapter<HistoryItem.Data> defaultAdapter = (TypeAdapter<HistoryItem.Data>) gson.getDelegateAdapter(this, type);
            return (TypeAdapter<T>) new DataAdapter(defaultAdapter);

        }

        public class DataAdapter extends TypeAdapter<HistoryItem.Data> {
            TypeAdapter<HistoryItem.Data> defaultAdapter;

            DataAdapter(TypeAdapter<HistoryItem.Data> defaultAdapter) {
                this.defaultAdapter = defaultAdapter;
            }
            @Override
            public void write(JsonWriter out, HistoryItem.Data value) throws IOException {
                defaultAdapter.write(out, value);
            }

            @Override
            public HistoryItem.Data read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.BOOLEAN) {
                    in.skipValue();
                    return null;
                }
                return defaultAdapter.read(in);
            }
        }
    }

    @Override
    public void bindItemViewHolder(final Context context, MyAdapter adapter, final MyAdapter.MyViewHolder holder, ListItem listItem, int position) {
        final int ITEM_TYPE     = 0;
        final int DIVIDER_TYPE  = 1;
        final int SPACE_TYPE    = 2;

        final String POST_ACTIVITY  = "postActivity";
        final String STATUS         = "status";
        final String JOIN           = "join";

        if(adapter.getItemViewType(position) == ITEM_TYPE) {
            HistoryItem historyItem = (HistoryItem) listItem;
            Resources res = getResources();
            holder.avatarView.update(context, mAvatarSuffix, mUserId, mUsername);
            holder.avatarView.scale(20);
            switch (historyItem.getType()) {
                case POST_ACTIVITY:
                    holder.content_tv.setHint(historyItem.getPostId());
                    if ("1".equals(historyItem.getStart())) {
                        holder.description_tv.setText(R.string.opened_a_conversation);
                        holder.content_tv.setText(historyItem.getTitle());
                    } else {
                        holder.description_tv.setText(res.getString(R.string.updated, historyItem.getTitle()));
                        holder.content_tv.setText(historyItem.getContent());
                    }
                    holder.description_tv.setHint(historyItem.getPostId());
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
                    holder.content_tv.setOnClickListener(onClickListener);
                    break;
                case STATUS:
                    holder.description_tv.setText(R.string.modified_his_or_her_information);
                    holder.content_tv.setText("");
                    HistoryItem.Data data = historyItem.getData();
                    if (data != null && data.getNewStatus() != null) {
                        holder.content_tv.setText(data.getNewStatus());
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

    /*@Override
    public Call<HistoryResult> getCall(int page){
        return MyRetrofit.getService().getHistory(mUserId, page);
    }*/

    @Override
    public String getURL() {
        return Constants.GET_ACTIVITIES_URL + mUserId;
    }
}
