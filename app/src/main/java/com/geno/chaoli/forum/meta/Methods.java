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

	public Channel getChannel(int channelId)
	{
		for (Channel c : Channel.values())
			if (c.getChannelId() == channelId) return c;
		return null;
	}

	public static TextView ChannelView(Context context, Channel channel)
	{
		TextView channelView = new TextView(context);
		int textColor;
		switch (channel.getChannelId())
		{
			case 4: textColor = 0xFF4030A0; break;
			case 5: textColor = 0xFFA00020; break;
			case 6: textColor = 0xFFE04000; break;
			case 7: textColor = 0xFF008000; break;
			case 8: textColor = 0xFF0040D0; break;
			case 9: textColor = 0xFF202020; break;
			case 36: textColor = 0xFFE04000; break;
			case 40: textColor = 0xFF9030C0; break;
			default: textColor = 0xFFA0A0A0;
		}
		channelView.setTextColor(textColor);
		channelView.setText(channel.toString());
		return channelView;
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
			/*result[i] = new ConversationView(context);
			result[i].conversation = new Conversation();
			result[i].conversation.conversationId = array.getJSONObject(i).getInteger("conversationId");*/
			Conversation conversation = new Conversation();
			conversation.conversationId = array.getJSONObject(i).getInteger("conversationId");
			result[i] = new ConversationView(context, conversation);
		}
		return result;
	}
}
