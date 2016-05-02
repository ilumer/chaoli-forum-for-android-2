package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geno.chaoli.forum.meta.AccountUtils.NotificationList;
import com.geno.chaoli.forum.meta.AccountUtils.Notification;
import com.geno.chaoli.forum.meta.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by daquexian on 16-5-2.
 * show all notifications.
 */
public class NotificationsActivity extends Activity {
    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        final ListView notifications_lv = (ListView) findViewById(R.id.lv_notifications);

        final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.loading));
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mContext, Constants.GET_ALL_NOTIFICATIONS_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                NotificationList notificationList = null;
                notifications_lv.setAdapter(new NotificationAdapter(notificationList));
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO: 16-5-2 show when failure 
                progressDialog.dismiss();
            }
        });
    }
    
    class NotificationAdapter extends BaseAdapter{
        NotificationList notificationList;

        NotificationAdapter(NotificationList notificationList){
            this.notificationList = notificationList;
        }

        @Override
        public int getCount() {
            return notificationList.count;
        }

        @Override
        public Object getItem(int position) {
            return notificationList.results.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.notification_item, null);
            }
            Notification thisNotification = (Notification)getItem(position);
            TextView description_tv = (TextView)convertView.findViewById(R.id.tv_description);
            TextView content_tv = (TextView)convertView.findViewById(R.id.tv_content);
            switch (thisNotification.type){
                // TODO: 16-5-2 description_tv.setText(..) 
            }
            content_tv.setText(thisNotification.data.title);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
