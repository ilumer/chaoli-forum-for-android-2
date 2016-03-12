package com.geno.chaoli.forum;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.Methods;

public class ConversationListFragment extends Fragment
{
	public static final String TAG = "ConversationListFrag";

	public String channel;

	public static LinearLayout l;

	public static Context context;

	public static SharedPreferences sp;

	public static TextView number;

	public String i;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View conversationListView = inflater.inflate(R.layout.conversation_list_fragment, container, false);
		context = getContext();
		l = (LinearLayout) conversationListView.findViewById(R.id.conversationList);
		number = new TextView(context);
		number.setText(i);
		l.addView(number);
		sp = getActivity().getSharedPreferences(Constants.conversationSP, Context.MODE_PRIVATE);
		Methods.getConversationList(getContext(), "/" + channel);
		Log.v(TAG, channel);
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

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}

	public static final void deal()
	{
		for (ConversationView c : Methods.dealConversationList(context, sp.getString(Constants.conversationSPKey, "")))
			l.addView(c);
	}

	public void setI(String i)
	{
		this.i = i;
	}

	public String getI()
	{
		return i;
	}

	/*
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser)
			Methods.getConversationList(getContext(), "/" + channel);
	}*/
}
