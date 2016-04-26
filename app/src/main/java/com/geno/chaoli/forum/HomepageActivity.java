package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.geno.chaoli.forum.meta.AccountUtils;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.LoginUtils;
import com.geno.chaoli.forum.pullableview.PullableScrollView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.cache.Resource;

/**
 * Created by jianhao on 16-4-14.
 */
public class HomepageActivity extends Activity implements PullableScrollView.ScrollListener,
        PullToRefreshLayout.OnRefreshListener{
    Context mContext;
    String mUsername = "待接收";
    int userId = -1; // to be received
    int mPage = 1;
    //第一条记录的时间
    String startTime = String.valueOf(Long.MIN_VALUE);
    //最后一条记录的时间
    String endTime = String.valueOf(Long.MAX_VALUE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mContext = this;
        ((TextView) findViewById(R.id.tv_username)).setText(mUsername);
        PullableScrollView scrollView = (PullableScrollView) findViewById(R.id.fssv_test);
        scrollView.setScrollListener((PullableScrollView.ScrollListener)mContext);
        final PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptrl_history);
        pullToRefreshLayout.setOnRefreshListener((PullToRefreshLayout.OnRefreshListener)mContext);
        pullToRefreshLayout.autoRefresh();
        //getNewActivities(1, pullToRefreshLayout);
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
    public void onScroll(int l, int t, int oldl, int oldt) {
        LinearLayout rootLayout = ((LinearLayout) findViewById(R.id.ll_homepage));
        final RelativeLayout top_rl = (RelativeLayout) findViewById(R.id.rl_top);
        int scrollY = Math.min(top_rl.getHeight(), (int)(t / 1.1));
        rootLayout.scrollTo(0, scrollY);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        final LinearLayout activities_ll = (LinearLayout) findViewById(R.id.ll_activities);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, Constants.GET_ACTIVITIES_URL + userId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Resources res = getResources();
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
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    TextView description_tv = new TextView(mContext);
                    description_tv.setSingleLine(true);
                    TextView content_tv = new TextView(mContext);
                    content_tv.setMaxLines(4);
                    content_tv.setMinLines(3);
                    content_tv.setEllipsize(TextUtils.TruncateAt.END);
                    TextView title_tv = new TextView(mContext);
                    title_tv.setEllipsize(TextUtils.TruncateAt.END);
                    title_tv.setTextSize(20);
                    title_tv.setSingleLine(true);
                    if("postActivity".equals(thisActivity.type)) {
                        content_tv.setText(thisActivity.content);
                        content_tv.setHint(thisActivity.postId);
                        if("1".equals(thisActivity.start)){
                            description_tv.setText(res.getString(R.string.opened_a_conversation, mUsername)); // TODO: 16-4-16 remove hardcode
                        }else{
                            description_tv.setText(res.getString(R.string.updated, mUsername));
                        }
                        title_tv.setText(thisActivity.title);
                        title_tv.setHint(thisActivity.postId);
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.get(mContext, "https://chaoli.club/index.php/conversation/post/" + ((TextView) v).getHint(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Log.d("body", new String(responseBody));
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.d("body", "e");
                                    }
                                });
                            }
                        };
                        title_tv.setOnClickListener(onClickListener);
                        linearLayout.addView(description_tv);
                        linearLayout.addView(title_tv);
                        linearLayout.addView(content_tv);
                    }else{
                        if("status".equals(thisActivity.type)){
                            description_tv.setText(res.getString(R.string.modified_his_or_her_information, mUsername));
                        }
                        MyActivity.Data data = JSON.parseObject(thisActivity.data, MyActivity.Data.class);
                        linearLayout.addView(description_tv);
                        if(data != null && data.newStatus != null){
                            content_tv.setText(data.newStatus);
                            linearLayout.addView(content_tv);
                        }
                    }
                    activities_ll.addView(linearLayout, 0);
                    //activities_ll.addView();
                }

                startTime = outerActivity.activity.get(0).time;

                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
            }
        });
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        final LinearLayout activities_ll = (LinearLayout) findViewById(R.id.ll_activities);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, Constants.GET_ACTIVITIES_URL + userId + "/" + (mPage + 1), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Resources res = getResources();
                String response = new String(responseBody);

                OuterActivity outerActivity = JSON.parseObject(response, OuterActivity.class);

                if(outerActivity.activity.size() == 0){
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
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
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    TextView description_tv = new TextView(mContext);
                    description_tv.setSingleLine(true);
                    TextView content_tv = new TextView(mContext);
                    content_tv.setMaxLines(4);
                    content_tv.setMinLines(3);
                    content_tv.setEllipsize(TextUtils.TruncateAt.END);
                    TextView title_tv = new TextView(mContext);
                    title_tv.setEllipsize(TextUtils.TruncateAt.END);
                    title_tv.setTextSize(20);
                    title_tv.setSingleLine(true);
                    if("postActivity".equals(thisActivity.type)) {
                        content_tv.setText(thisActivity.content);
                        content_tv.setHint(thisActivity.postId);
                        if("1".equals(thisActivity.start)){
                            description_tv.setText(res.getString(R.string.opened_a_conversation, mUsername)); // TODO: 16-4-16 remove hardcode
                        }else{
                            description_tv.setText(res.getString(R.string.updated, mUsername));
                        }
                        title_tv.setText(thisActivity.title);
                        title_tv.setHint(thisActivity.postId);
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.get(mContext, "https://chaoli.club/index.php/conversation/post/" + ((TextView) v).getHint(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Log.d("body", new String(responseBody));
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.d("body", "e");
                                    }
                                });
                            }
                        };
                        title_tv.setOnClickListener(onClickListener);
                        linearLayout.addView(description_tv);
                        linearLayout.addView(title_tv);
                        linearLayout.addView(content_tv);
                    }else{
                        if("status".equals(thisActivity.type)){
                            description_tv.setText(res.getString(R.string.modified_his_or_her_information, mUsername));
                        }
                        MyActivity.Data data = JSON.parseObject(thisActivity.data, MyActivity.Data.class);
                        content_tv.setText(data.newStatus);
                        linearLayout.addView(description_tv);
                        linearLayout.addView(content_tv);
                    }
                    activities_ll.addView(linearLayout);
                    //activities_ll.addView();
                }

                endTime = outerActivity.activity.get(i - 1).time;
                mPage++;

                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        });
    }
}
