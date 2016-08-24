package com.geno.chaoli.forum;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.ChannelTextView;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.Conversation;
import com.geno.chaoli.forum.meta.ConversationView;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ConversationListFragment extends Fragment
{
	public static final String TAG = "ConversationListFrag";

	public String channel;

	public static RecyclerView l;

	public static Context context;

	public static SharedPreferences sp;

	public static AsyncHttpClient client = new AsyncHttpClient();

	public ConversationView[] v;

	public SwipyRefreshLayout swipyRefreshLayout;

	ConversationRecyclerViewAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View conversationListView = inflater.inflate(R.layout.conversation_list_fragment, container, false);
		context = getActivity();
		l = (RecyclerView) conversationListView.findViewById(R.id.conversationList);
		sp = getActivity().getSharedPreferences(Constants.conversationSP, Context.MODE_PRIVATE);
		swipyRefreshLayout = (SwipyRefreshLayout) conversationListView.findViewById(R.id.conversationListRefreshLayout);
		swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
		refresh();
		swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh(SwipyRefreshLayoutDirection direction)
			{
				getList();
			}
		});

		l.setLayoutManager(new LinearLayoutManager(context));
		mAdapter = new ConversationRecyclerViewAdapter(context, new ArrayList<Conversation>());
		l.setAdapter(mAdapter);

		return conversationListView;
	}

	public void refresh(){
		//trigger the circle to animate
		swipyRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipyRefreshLayout.setRefreshing(true);
			}
		});
		getList();
	}

	public void getList()
	{
		CookieUtils.saveCookie(client, context);
		client.get(context, Constants.conversationListURL + channel, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				JSONObject o = JSON.parseObject(new String(responseBody));
				JSONArray array = o.getJSONArray("results");

				List<Conversation> conversationList = new ArrayList<Conversation>();
				for (int i = 0; i < array.size(); i++)
				{
					JSONObject sub = array.getJSONObject(i);
					Conversation c = new Conversation();
					c.conversationId = sub.getInteger("conversationId");
					c.title = sub.getString("title");
					c.excerpt = sub.getString("firstPost");
					c.replies = sub.getInteger("replies");
					c.startMember = sub.getString("startMember");
					c.startMemberAvatarSuffix = sub.getString("startMemberAvatarFormat");
					c.startMemberId = sub.getString("startMemberId");
					c.lastPostMember = sub.getString("lastPostMember");
					c.lastPostMemberAvatarSuffix = sub.getString("lastPostMemberAvatarFormat");
					c.lastPostMemberId = sub.getString("lastPostMemberId");
					c.channel = Channel.getChannel(sub.getInteger("channelId"));
					conversationList.add(c);
					//v[i] = new ConversationView(getActivity(), c);
				}
				DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(mAdapter.getConversationList(), conversationList), true);
				mAdapter.setConversationList(conversationList);
				diffResult.dispatchUpdatesTo(mAdapter);
				//l.setAdapter(new ConversationRecyclerViewAdapter(context, conversationList));
				/*l.setAdapter(new BaseAdapter()
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
				});*/
				swipyRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_SHORT).show();
				swipyRefreshLayout.setRefreshing(false);
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

	public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ConversationViewHolder> {
		List<Conversation> conversationList = new ArrayList<>();
		LayoutInflater layoutInflater;

		public ConversationRecyclerViewAdapter(Context context, List<Conversation> conversationList){
			layoutInflater = LayoutInflater.from(context);
			this.conversationList = conversationList;
		}

		public List<Conversation> getConversationList() {
			return conversationList;
		}

		public void setConversationList(List<Conversation> conversationList) {
			this.conversationList = new ArrayList<>(conversationList);
		}

		@Override
		public void onBindViewHolder(ConversationViewHolder holder, int position) {
			Conversation conversation = conversationList.get(position);
			holder.avatarView.update(context, conversation.getStartMemberAvatarSuffix(),
					Integer.parseInt(conversation.getStartMemberId()), conversation.getStartMember());
			holder.avatarView.scale(20);
			holder.usernameTv.setText(conversation.getStartMember() + " 发表了帖子");
			//((TextView) findViewById(R.id.conversationId)).setText(String.format(Locale.getDefault(), "%d", conversation.getConversationId()));
			holder.titleTv.setText(conversation.getTitle());
			String excerpt = conversation.getExcerpt();//.split("\\n")[0];  TextView有一个参数是可以自动把多出的字符变成...的(见xml文件),所以这个就不需要啦
			holder.excerptTv.setText(excerpt);
			//((TextView) findViewById(R.id.excerpt)).setText(excerpt.length() > 50 ?
			//		excerpt.substring(0, 50) + "…" : excerpt);
//		((TextView) findViewById(R.id.replies)).setText(String.format(Locale.getDefault(), "%d", conversation.getReplies()));
			holder.channel.removeAllViews();
			holder.channel.addView(new ChannelTextView(context, conversation.getChannel()));
			holder.replyNumTv.setText(String.valueOf(conversation.getReplies()));
			holder.conversation = conversationList.get(position);
		}

		@Override
		public int getItemCount() {
			return conversationList.size();
		}

		@Override
		public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ConversationViewHolder(layoutInflater.inflate(R.layout.conversation_view, parent, false));
		}

		public class ConversationViewHolder extends RecyclerView.ViewHolder{
			@BindView(R.id.avatar)
			AvatarView avatarView;
			@BindView(R.id.username)
			TextView usernameTv;
			@BindView(R.id.title)
			TextView titleTv;
			@BindView(R.id.reply_num)
			TextView replyNumTv;
			@BindView(R.id.excerpt)
			TextView excerptTv;
			@BindView(R.id.channel)
			LinearLayout channel;

			Conversation conversation;

			public ConversationViewHolder(View view){
				super(view);

				ButterKnife.bind(this, view);

				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent jmp = new Intent();
						jmp.putExtra("conversationId", conversation.getConversationId());
						jmp.putExtra("title", conversation.getTitle());
						jmp.setClass(context, PostActivity.class);
						context.startActivity(jmp);
					}
				});
				view.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						LinearLayout menuList = new LinearLayout(context);
						menuList.setPadding(Constants.paddingLeft, Constants.paddingTop,
								Constants.paddingRight, Constants.paddingBottom);

						AlertDialog.Builder menuBuilder = new AlertDialog.Builder(context).setView(menuList);
						final Dialog menu = menuBuilder.create();
						return true;
					}
				});
			}

		}
	}

	private class DiffCallback extends DiffUtil.Callback {
		List<Conversation> oldConversationList, newConversationList;

		DiffCallback(List<Conversation> oldConversationList, List<Conversation> newConversationList){
			this.oldConversationList = oldConversationList;
			this.newConversationList = newConversationList;
		}

		@Override
		public int getNewListSize() {
			return newConversationList.size();
		}

		@Override
		public int getOldListSize() {
			return oldConversationList.size();
		}

		@Override
		public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
			Log.d(TAG, String.valueOf(oldConversationList.get(oldItemPosition).conversationId == newConversationList.get(newItemPosition).conversationId));
			return oldConversationList.get(oldItemPosition).conversationId == newConversationList.get(newItemPosition).conversationId;
		}

		@Override
		public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
			return true;
		}
	}
}
