package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.FullScreenObservableScrollView;
import com.geno.chaoli.forum.meta.ScrollViewListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by daquexian on 16-4-14.
 */
public class HomepageActivity extends Activity implements SwipyRefreshLayout.OnRefreshListener, ScrollViewListener{
    final String TAG = "HomepageActivity";

    Context mContext;
    String mUsername; // to be received
    String mSignature; // to be received
    int userId; // to be received
    int mPage = 1;
    //String avatarURL; // to be received
    String avatarSuffix; // use this if avatarURL is null
    //第一条记录的时间
    String startTime = String.valueOf(Long.MIN_VALUE);
    //最后一条记录的时间
    String endTime = String.valueOf(Long.MAX_VALUE);

    SwipyRefreshLayout srl_activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            Log.e(TAG, "bundle mustn't be null");
            this.finish();
            return;
        }
        mUsername = bundle.getString("username", "");
        mSignature = bundle.getString("signature", "");
        userId = bundle.getInt("userId", -1);
        //avatarURL = bundle.getString("avatarURL", "");
        avatarSuffix = bundle.getString("avatarSuffix", Constants.NONE);

        /*Log.d(TAG, "id=" + userId + "un=" + mUsername + "url=" + avatarURL + "suffix=" + avatarSuffix);
        if("".equals(avatarURL) && avatarSuffix != null && !"".equals(avatarSuffix)){
            avatarURL = Constants.avatarURL + "avatar_" + userId + "." + avatarSuffix;
        }*/

        if("".equals(mUsername) || userId == -1){
            this.finish();
            return;
        }

        FullScreenObservableScrollView osv_activities = (FullScreenObservableScrollView)findViewById(R.id.osv_activities);
        osv_activities.setScrollViewListener(this);

        ((TextView) findViewById(R.id.tv_username)).setText(mUsername);
        ((TextView) findViewById(R.id.tv_signature)).setText(mSignature);
        AvatarView avatar_iv = (AvatarView) findViewById(R.id.iv_avatar);
        avatar_iv.update(mContext, avatarSuffix, userId, mUsername);
        srl_activities = (SwipyRefreshLayout) findViewById(R.id.srl_activities);
        srl_activities.setDirection(SwipyRefreshLayoutDirection.BOTH);
        srl_activities.setOnRefreshListener(this);

        srl_activities.post(new Runnable() {
            @Override
            public void run() {
                srl_activities.setRefreshing(true);
                onRefresh(SwipyRefreshLayoutDirection.TOP); // why is it also needed?
            }
        });
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
    }

    @Override
    public void onScrollChanged(FullScreenObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(scrollView.getId() == R.id.osv_activities){
            LinearLayout rootLayout = ((LinearLayout) findViewById(R.id.ll_homepage));
            final RelativeLayout top_rl = (RelativeLayout) findViewById(R.id.rl_top);
            int scrollY = Math.min(top_rl.getHeight(), (int)(y / 1.1));
            rootLayout.scrollTo(0, scrollY);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if(direction == SwipyRefreshLayoutDirection.TOP){
            final LinearLayout activities_ll = (LinearLayout) findViewById(R.id.ll_activities);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(this, Constants.GET_ACTIVITIES_URL + userId, new AsyncHttpResponseHandler() {
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
                        LinearLayout linearLayout = inflateItem(thisActivity);
                        activities_ll.addView(linearLayout, 0);
                        //activities_ll.addView();
                    }

                    startTime = outerActivity.activity.get(0).time;

                    srl_activities.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    srl_activities.setRefreshing(false);
                }
            });
        }else{
            final LinearLayout activities_ll = (LinearLayout) findViewById(R.id.ll_activities);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(this, Constants.GET_ACTIVITIES_URL + userId + "/" + (mPage + 1), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Resources res = getResources();
                    String response = new String(responseBody);

                    OuterActivity outerActivity = JSON.parseObject(response, OuterActivity.class);

                    if(outerActivity.activity.size() == 0){
                        srl_activities.setRefreshing(false);
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
                        LinearLayout linearLayout = inflateItem(thisActivity);
                        activities_ll.addView(linearLayout);
                        //activities_ll.addView();
                    }

                    endTime = outerActivity.activity.get(i - 1).time;
                    mPage++;

                    srl_activities.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    srl_activities.setRefreshing(false);
                }
            });
        }
    }

    private LinearLayout inflateItem(MyActivity thisActivity){
        Resources res = getResources();
        Log.i(TAG, "inflateItem");
        LinearLayout ll = (LinearLayout)getLayoutInflater().inflate(R.layout.history_item, null);
        final TextView content_tv = (TextView) ll.findViewById(R.id.tv_post_content);
        TextView description_tv = (TextView) ll.findViewById(R.id.tv_description);
        TextView title_tv = (TextView) ll.findViewById(R.id.tv_conversation_title);
        /*LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView description_tv = new TextView(mContext);
        description_tv.setSingleLine(true);
        final TextView content_tv = new TextView(mContext);
        content_tv.setMaxLines(4);
        content_tv.setMinLines(3);
        content_tv.setEllipsize(TextUtils.TruncateAt.END);
        final TextView title_tv = new TextView(mContext);
        title_tv.setEllipsize(TextUtils.TruncateAt.END);
        title_tv.setTextSize(20);
        title_tv.setSingleLine(true);*/
        if("postActivity".equals(thisActivity.type)) {
            content_tv.setText(thisActivity.content);
            content_tv.setHint(thisActivity.postId);
            if("1".equals(thisActivity.start)){
                description_tv.setText(res.getString(R.string.opened_a_conversation, mUsername));
            }else{
                description_tv.setText(res.getString(R.string.updated, mUsername));
            }
            title_tv.setText(thisActivity.title);
            title_tv.setHint(thisActivity.postId);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.just_a_sec));
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(mContext, "https://chaoli.club/index.php/conversation/post/" + ((TextView) v).getHint(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d("body", new String(responseBody));
                            String response = new String(responseBody);
                            Intent intent = new Intent(HomepageActivity.this, PostActivity.class);

                            Pattern pattern = Pattern.compile("\"conversationId\":(\\d+)");
                            Matcher matcher = pattern.matcher(response);
                            if(matcher.find()){
                                int conversationId = Integer.parseInt(matcher.group(1));
                                intent.putExtra("conversationId", conversationId);
                            }

                            pattern = Pattern.compile("<h1 id='conversationTitle'>(.*?)</h1>");
                            matcher = pattern.matcher(response);
                            if(matcher.find()){
                                String title = matcher.group(1);
                                intent.putExtra("title", title);
                            }

                            if(v.equals(content_tv)) {
                                pattern = Pattern.compile("\"startFrom\":(\\d+)");
                                matcher = pattern.matcher(response);
                                if (matcher.find()) {
                                    String intentToPage = "/p" + String.valueOf(Integer.parseInt(matcher.group(1)) / 20 + 1);
                                    Log.d("page", intentToPage);
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
            title_tv.setOnClickListener(onClickListener);
            content_tv.setOnClickListener(onClickListener);
        }else{
            if("status".equals(thisActivity.type)){
                description_tv.setText(res.getString(R.string.modified_his_or_her_information, mUsername));
            }
            MyActivity.Data data = JSON.parseObject(thisActivity.data, MyActivity.Data.class);
            ll.removeView(title_tv);
            if(data != null && data.newStatus != null){
                content_tv.setText(data.newStatus);
            }
        }
        return ll;
    }
}
