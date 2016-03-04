package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.Methods;

import java.lang.ref.WeakReference;

public class ConversationListFragment extends Fragment
{
	public static final String TAG = "ConversationListFrag";

	public String channel;

	public LinearLayout l;

	public SharedPreferences sp;

	public final ApplicationHandler handler = new ApplicationHandler(getActivity(), l, getActivity(), sp);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View conversationListView = inflater.inflate(R.layout.conversationlistfragment, container, false);
		l = (LinearLayout) conversationListView.findViewById(R.id.conversationList);
		sp = getActivity().getSharedPreferences(Constants.conversationSP, Context.MODE_PRIVATE);
		Methods.getConversationList(getContext(), "/" + channel, handler, sp);
		return conversationListView;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public static final void deal(Context context, Activity activity, LinearLayout l, SharedPreferences sp)
	{
		for (ConversationView c : Methods.dealConversationList(context, sp.getString(Constants.conversationSPKey, "")))
		{
			l.addView(c);
		}
	}
}
