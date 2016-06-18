package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.geno.chaoli.forum.meta.AccountUtils.NotificationList;
import com.geno.chaoli.forum.meta.AccountUtils.Notification;
import com.geno.chaoli.forum.meta.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by daquexian on 16-5-2.
 * show all notifications.
 */
public class NotificationsActivity extends BaseActivity {
    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        final RecyclerView notifications_rv = (RecyclerView) findViewById(R.id.rv_notifications);

        final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.loading));
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mContext, Constants.GET_ALL_NOTIFICATIONS_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                NotificationList notificationList = JSON.parseObject(response, NotificationList.class);
                notifications_rv.setAdapter(new NotificationAdapter(notificationList));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO: 16-5-2 show when failure
                Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{
        NotificationList notificationList;

        static final String TYPE_MENTION        = "mention";
        static final String TYPE_POST           = "post";
        static final String TYPE_PRIVATE_ADD    = "privateAdd";

        NotificationAdapter(NotificationList notificationList){
            this.notificationList = notificationList;
        }

        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            Notification thisNotification = notificationList.results.get(position);

            switch (thisNotification.type){
                // TODO: 16-5-2 description_tv.setText(..)
                case TYPE_MENTION:
                    holder.tvDescription.setText(getString(R.string.mention_you, thisNotification.fromMemberName));
                    break;
                case TYPE_POST:
                    holder.tvDescription.setText(getString(R.string.someone_update, thisNotification.fromMemberName));
                    break;
                case TYPE_PRIVATE_ADD:
                    holder.tvDescription.setText(getString(R.string.send_you_a_private_post, thisNotification.fromMemberName));
                    break;
            }
            holder.tvContent.setText(thisNotification.data.title);

            holder.tvDescription.setHint(thisNotification.fromMemberId);
            holder.tvContent.setHint(thisNotification.data.postId);
        }

        @Override
        public int getItemCount() {
            return notificationList.count;
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotificationViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false));
        }

        public class NotificationViewHolder extends RecyclerView.ViewHolder{
            TextView tvDescription, tvContent;
            NotificationViewHolder(View view){
                super(view);
                tvDescription = (TextView) view.findViewById(R.id.tv_description);
                tvContent = (TextView)view.findViewById(R.id.tv_content);

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.tv_description:
                                
                                break;
                            case R.id.tv_content:
                                final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.login));// TODO: 16-6-18 change it
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.get(mContext, "https://chaoli.club/index.php/conversation/post/" + ((TextView) v).getHint(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        String response = new String(responseBody);
                                        Intent intent = new Intent(mContext, PostActivity.class);

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

                                            pattern = Pattern.compile("\"startFrom\":(\\d+)");
                                            matcher = pattern.matcher(response);
                                            if (matcher.find()) {
                                                String intentToPage = "/p" + String.valueOf(Integer.parseInt(matcher.group(1)) / 20 + 1);
                                                intent.putExtra("intentToPage", intentToPage);
                                            }
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        progressDialog.dismiss();
                                    }
                                });
                                break;
                        }
                    }
                };
            }
        }
    }
}
