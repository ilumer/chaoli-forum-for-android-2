package com.geno.chaoli.forum;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.Conversation;
import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ConversationListFragment extends Fragment
{
	public static final String TAG = "ConversationListFrag";

	public String channel;

	public static ListView l;

	public static Context context;

	public static SharedPreferences sp;

	public static AsyncHttpClient client = new AsyncHttpClient();

	public ConversationView[] v;

	public SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View conversationListView = inflater.inflate(R.layout.conversation_list_fragment, container, false);
		context = getActivity();
		l = (ListView) conversationListView.findViewById(R.id.conversationList);
		Log.v(TAG, getActivity() + "");
		sp = getActivity().getSharedPreferences(Constants.conversationSP, Context.MODE_PRIVATE);
		Log.v(TAG, channel + ".");
		swipeRefreshLayout = (SwipeRefreshLayout) conversationListView.findViewById(R.id.conversationListRefreshLayout);
		swipeRefreshLayout.setRefreshing(true);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				getList();
			}
		});
		getList();

		return conversationListView;
	}

	public void getList()
	{
		CookieUtils.saveCookie(client, context);
		client.get(context, Constants.conversationListURL + "/" + channel, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				JSONObject o = JSON.parseObject(new String(responseBody));
				JSONArray array = o.getJSONArray("results");
				v = new ConversationView[array.size()];
				for (int i = 0; i < array.size(); i++)
				{
					JSONObject sub = array.getJSONObject(i);
					Conversation c = new Conversation();
					c.conversationId = sub.getInteger("conversationId");
					c.title = sub.getString("title");
					c.excerpt = sub.getString("firstPost");
					c.replies = sub.getInteger("replies");
					c.channel = Channel.getChannel(sub.getInteger("channelId"));
					v[i] = new ConversationView(getActivity(), c);
				}
				l.setAdapter(new BaseAdapter()
				{
					@Override
					public int getCount()
					{
						return v.length;
					}

					@Override
					public Object getItem(int position)
					{
						return v[position];
					}

					@Override
					public long getItemId(int position)
					{
						return v[position].conversation.conversationId;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent)
					{
						return v[position];
					}
				});
				swipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_SHORT).show();
				swipeRefreshLayout.setRefreshing(false);
			}
		});
	}

	public String getChannel()
	{
		return channel;
	}

	public ConversationListFragment setChannel(String channel)
	{
		this.channel = channel;
		return this;
	}
}
