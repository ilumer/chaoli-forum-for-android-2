package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geno.chaoli.forum.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Methods
{
	public static final String TAG = "Methods";

	public static final Channel getChannel(int channelId)
	{
		for (Channel c : Channel.values())
			if (c.getChannelId() == channelId) return c;
		return null;
	}

/*
	public static void getList(final Context context, final Handler handler)
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					URL url = new URL(Constants.conversationListURL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
					{
						InputStream i = connection.getInputStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(i));
						SharedPreferences sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
						SharedPreferences.Editor e = sp.edit();
						String content = sp.getString("content", "");
						Log.d(TAG, content);
						JSONObject obj = JSON.parseObject(content);
						JSONArray arr = obj.getJSONArray("results");
						for (int j = 0; j < arr.size(); j++)
						{
							e.putStringSet(arr.getJSONObject(j).getInteger("conversationId") + "", arr.getJSONObject(j).keySet());
						}
						e.commit();
						Log.d(TAG, arr + "");
					}
					connection.disconnect();
					handler.sendEmptyMessage(0);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		t.start();
	}*/

	public static void getList(final Context context)
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				String result;
				SharedPreferences sp = context.getSharedPreferences("conversationList", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				super.run();
				try
				{
					URL url = new URL(Constants.conversationListURL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					switch (connection.getResponseCode())
					{
						case HttpURLConnection.HTTP_OK:
							InputStream i = connection.getInputStream();
							BufferedReader br = new BufferedReader(new InputStreamReader(i));
							result = br.readLine();
							// TODO: 2016/2/24 0024 0144 Save metadata of a conversation instead of a JSON string.
							editor.putString("listJSON", result);
							editor.apply();
							Log.v(TAG, result);
							break;
						default:
					}
					connection.disconnect();
					Looper.prepare();
					((MainActivity) context).mainHandler.sendEmptyMessage(0);
					Log.v(TAG, "Method finish");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public static ConversationView[] dealList(Context context, String string)
	{
		JSONObject o = JSON.parseObject(string);
		Log.v(TAG, o + "");
		JSONArray array = o.getJSONArray("results");
		//Map<Integer, Map<String, String>> result = new HashMap<>(array.size());
		ConversationView[] result = new ConversationView[array.size()];
		Log.v(TAG, array.size() + "");
		for (int i = 0; i < array.size(); i++)
		{
			JSONObject sub = array.getJSONObject(i);
			Conversation conversation = new Conversation();
			conversation.conversationId = sub.getInteger("conversationId");
			conversation.title = sub.getString("title");
			conversation.excerpt = sub.getString("firstPost");
			conversation.replies = sub.getInteger("replies");
			conversation.channel = Methods.getChannel(Integer.parseInt(sub.getString("channelId")));
			result[i] = new ConversationView(context, conversation);
		}
		return result;
	}
}
