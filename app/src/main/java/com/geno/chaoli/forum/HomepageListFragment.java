package com.geno.chaoli.forum;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.network.MyOkHttp;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jianhao on 16-6-5.
 */
abstract public class HomepageListFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout mSwipyRefreshLayout;
    RecyclerView rvHistory;
    MyAdapter myAdapter;
    Context mCallback;
    String startTime = String.valueOf(Long.MIN_VALUE);  //第一条记录的时间
    String endTime = String.valueOf(Long.MAX_VALUE);    //最后一条记录的时间
    int mPage = 1;
    Boolean bottom = true;                                     //判断RecyclerView是否滑动到底部

    final static String TAG = "HomepageListFragment";

    abstract public String getURL();
    //abstract public retrofit2.Call<> getCall(int page, Class T);
    abstract public List<? extends ListItem> parseItems(String JSONString);
    abstract public void bindItemViewHolder(Context context, MyAdapter adapter, MyAdapter.MyViewHolder holder, ListItem listItem, int position);

    /*public retrofit2.Call<List<ListItem>> getCall(){
        return getCall(1);
    }*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = context;
        myAdapter = new MyAdapter(mCallback, new ArrayList<ListItem>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipyRefreshLayout = (SwipyRefreshLayout) inflater.inflate(R.layout.homepage_history, container, false);

        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        mSwipyRefreshLayout.setOnRefreshListener(this);
        mSwipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipyRefreshLayout.setRefreshing(true);
                onRefresh(SwipyRefreshLayoutDirection.TOP); // why is it also needed?
            }
        });

        rvHistory = (RecyclerView) mSwipyRefreshLayout.findViewById(R.id.rvHomepageItems);
        rvHistory.setLayoutManager(new LinearLayoutManager(mCallback));
        rvHistory.setAdapter(myAdapter);

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
        return mSwipyRefreshLayout;
    }
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        final SwipyRefreshLayout swipyRefreshLayout = mSwipyRefreshLayout;
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            new MyOkHttp.MyOkHttpClient()
                    .get(getURL())
                    .enqueue(mCallback, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            swipyRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseStr = response.body().string();
                            Log.d(TAG, responseStr);
                            List<? extends ListItem> listItems = parseItems(responseStr);

                            int i = 0;
                            for (; i < listItems.size(); i++) {
                                if (Long.parseLong(startTime) >= Long.parseLong(listItems.get(i).getTime())) {
                                    break;
                                }
                            }

                            i--;

                            for (; i >= 0; i--) {
                                ListItem listItem = listItems.get(i);
                                myAdapter.listItems.add(0, listItem);
                            }

                            startTime = listItems.size() > 0 ? listItems.get(0).getTime() : startTime;

                            Log.d(TAG, String.valueOf(myAdapter.listItems.size()));
                            addTimeDivider(myAdapter.listItems);
                            myAdapter.notifyDataSetChanged();
                            swipyRefreshLayout.setRefreshing(false);

                        }
                    });
        } else {
            new MyOkHttp.MyOkHttpClient()
                    .get(getURL() + "/" + (mPage + 1))
                    .enqueue(mCallback, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            swipyRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseStr = response.body().string();
                            Log.d(TAG, "onResponse: " + responseStr);
                            List<? extends ListItem> listItems = parseItems(responseStr);

                            final int listSize = listItems.size();
                            if(listSize == 0){
                                swipyRefreshLayout.setRefreshing(false);
                                return;
                            }

                            int i = 0;
                            for(; i < listSize; i++){
                                if(Long.parseLong(endTime) > Long.parseLong(listItems.get(i).getTime())){
                                    break;
                                }
                            }

                            for (; i < listSize; i++) {
                                ListItem listItem = listItems.get(i);
                                myAdapter.listItems.add(listItem);
                            }

                            endTime = listItems.get(i - 1).getTime();
                            mPage++;

                            swipyRefreshLayout.setRefreshing(false);

                            addTimeDivider(myAdapter.listItems);
                            myAdapter.notifyDataSetChanged();

                        }
                    });
        }
        /*AsyncHttpClient client = new AsyncHttpClient();
        CookieUtils.saveCookie(client, mCallback);
        if(direction == SwipyRefreshLayoutDirection.TOP){
            client.get(mCallback, getURL(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    Log.d(TAG, response);
                    List<? extends ListItem> listItems = parseItems(response);

                    int i = 0;
                    for (; i < listItems.size(); i++) {
                        if (Long.parseLong(startTime) >= Long.parseLong(listItems.get(i).getTime())) {
                            break;
                        }
                    }

                    i--;

                    for (; i >= 0; i--) {
                        ListItem listItem = listItems.get(i);
                        myAdapter.listItems.add(0, listItem);
                    }

                    startTime = listItems.size() > 0 ? listItems.get(0).getTime() : startTime;

                    Log.d(TAG, String.valueOf(myAdapter.listItems.size()));
                    addTimeDivider(myAdapter.listItems);
                    myAdapter.notifyDataSetChanged();
                    swipyRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    swipyRefreshLayout.setRefreshing(false);
                }
            });
        }else{
            String url = getURL() + "/" + (mPage + 1);
            Log.d(TAG, url);
            client.get(mCallback, url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    List<? extends ListItem> listItems = parseItems(response);

                    final int listSize = listItems.size();
                    if(listSize == 0){
                        swipyRefreshLayout.setRefreshing(false);
                        return;
                    }

                    int i = 0;
                    for(; i < listSize; i++){
                        if(Long.parseLong(endTime) > Long.parseLong(listItems.get(i).getTime())){
                            break;
                        }
                    }

                    for (; i < listSize; i++) {
                        ListItem listItem = listItems.get(i);
                        myAdapter.listItems.add(listItem);
                    }

                    endTime = listItems.get(i - 1).getTime();
                    mPage++;

                    swipyRefreshLayout.setRefreshing(false);

                    addTimeDivider(myAdapter.listItems);
                    myAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    swipyRefreshLayout.setRefreshing(false);
                }
            });
        }*/
    }

    public void setRefreshEnabled(Boolean enabled){
        mSwipyRefreshLayout.setEnabled(enabled || bottom);
    }

    public void setDirection(SwipyRefreshLayoutDirection direction){
        mSwipyRefreshLayout.setDirection(direction);
    }

    public abstract static class ListItem{
        public final static String ITEM         = "item";
        public final static String DIVIDER      = "divider";
        public final static String SPACE        = "space";

        public String getType() {
            return type;
        }

        public String getTime() {
            return time;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTime(String time) {
            this.time = time;
        }

        String type, time;
    }

    static class Divider extends ListItem{
        String time;
        @Override
        public String getType() {
            return ListItem.DIVIDER;
        }

        @Override
        public String getTime() {
            return time;
        }

        Divider(int time){
            this.time = String.valueOf(time);
        }
    }

    static class Space extends ListItem{
        @Override
        public String getTime() {
            return null;
        }

        @Override
        public String getType() {
            return ListItem.SPACE;
        }
    }

    private void addTimeDivider(List<ListItem> items){
        int dateNow;
        if(items.size() > 0 && !ListItem.DIVIDER.equals(items.get(0).getType()) && !ListItem.SPACE.equals(items.get(0).getType())){
            int firstDate = (int) ((Long.parseLong(items.get(0).getTime()) + 8 * 60 * 60) / 24 / 60 / 60);
            dateNow = (int) (Calendar.getInstance().getTimeInMillis() / 1000 / 24 / 60 / 60);
            items.add(0, new Divider(dateNow - firstDate));
        }
        if(items.size() > 0 && !(items.get(0) instanceof Space)) items.add(0, new Space());
        for(int i = 0; i < items.size() - 1; i++){
            ListItem thisItem = items.get(i), nextItem = items.get(i + 1);
            if(!ListItem.DIVIDER.equals(thisItem.getType()) && !ListItem.DIVIDER.equals(nextItem.getType())
                    && !ListItem.SPACE.equals(thisItem.getType()) && !ListItem.SPACE.equals(nextItem.getType())){
                int thisDate = (int) ((Long.parseLong(thisItem.getTime()) + 8 * 60 * 60) / 24 / 60 / 60);
                int nextDate = (int) ((Long.parseLong(nextItem.getTime()) + 8 * 60 * 60) / 24 / 60 / 60);
                if(thisDate != nextDate) {
                    dateNow = (int) (Calendar.getInstance().getTimeInMillis() / 1000 / 24 / 60 / 60);
                    items.add(i + 1, new Divider(dateNow - nextDate));
                }
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        Context mContext;
        List<ListItem> listItems;

        final int ITEM_TYPE     = 0;
        final int DIVIDER_TYPE  = 1;
        final int SPACE_TYPE    = 2;

        MyAdapter(Context context, List<ListItem> listItems){
            mContext = context;
            this.listItems = listItems;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutId = viewType == ITEM_TYPE ? R.layout.history_item : viewType == DIVIDER_TYPE ? R.layout.history_divider : R.layout.history_space;
            return new MyViewHolder(LayoutInflater.from(mCallback).
                    inflate(layoutId, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ListItem listItem = listItems.get(position);
            bindItemViewHolder(mContext, this, holder, listItem, position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView content_tv, description_tv, time_tv;
            AvatarView avatarView;
            View divider;
            MyViewHolder(View view){
                super(view);
                // For item
                content_tv = (TextView) view.findViewById(R.id.tv_post_content);
                description_tv = (TextView) view.findViewById(R.id.tv_description);
                avatarView = (AvatarView) view.findViewById(R.id.avatar);
                // For divider
                time_tv = (TextView) view.findViewById(R.id.tvTime);
                divider = view.findViewById(R.id.divider);
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (listItems.get(position).getType()){
                case ListItem.DIVIDER:
                    return DIVIDER_TYPE;
                case ListItem.SPACE:
                    return SPACE_TYPE;
                default:
                    return ITEM_TYPE;
            }
        }
    }

    public Boolean isBottom(){
        return bottom;
    }
}
