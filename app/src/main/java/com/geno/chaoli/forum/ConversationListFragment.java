package com.geno.chaoli.forum;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConversationListFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View conversationListView = inflater.inflate(R.layout.conversationlistfragment, container, false);
		return conversationListView;
	}
}
