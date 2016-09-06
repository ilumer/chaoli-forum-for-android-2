package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.AvatarView;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.ChannelTextView;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.DividerItemDecoration;
import com.geno.chaoli.forum.model.Conversation;
import com.geno.chaoli.forum.model.ConversationListResult;
import com.geno.chaoli.forum.network.MyRetrofit;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationListFragment extends Fragment
{
	public static final String TAG = "ConversationListFrag";

	private int mPage = 1;

	public String channel;

	RecyclerView l;

	Context mContext;

	SharedPreferences sp;

	public SwipyRefreshLayout swipyRefreshLayout;

	ConversationRecyclerViewAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View conversationListView = inflater.inflate(R.layout.conversation_list_fragment, container, false);
		mContext = getActivity();
		l = (RecyclerView) conversationListView.findViewById(R.id.conversationList);
		sp = getActivity().getSharedPreferences(Constants.conversationSP, Context.MODE_PRIVATE);
		swipyRefreshLayout = (SwipyRefreshLayout) conversationListView.findViewById(R.id.conversationListRefreshLayout);
		swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);

		//trigger the circle to animate
		swipyRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipyRefreshLayout.setRefreshing(true);
			}
		});
		refresh();

		swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh(SwipyRefreshLayoutDirection direction)
			{
				if (direction == SwipyRefreshLayoutDirection.TOP) {
					refresh();
				} else {
					getList(mPage += 1);
				}
			}
		});

		l.setLayoutManager(new LinearLayoutManager(mContext));
		l.addItemDecoration(new DividerItemDecoration(mContext));
		mAdapter = new ConversationRecyclerViewAdapter(mContext, new ArrayList<Conversation>());
		l.setAdapter(mAdapter);

		return conversationListView;
	}

	public void refresh(){
		getList(1);
	}

	/**
	 * 针对可能有其他帖子被顶到最上方，导致下一页的主题帖与这一页的主题帖有重合的现象
	 * @param A 已有的主题帖列表
	 * @param B 下一页主题帖列表
     * @return 合成后的新列表的长度
     */
	private int expandUnique(List<Conversation> A, List<Conversation> B) {
		int lenA = A.size();
		if (lenA == 0) {
			A.addAll(B);
		}
		int i;
		for (i = 0; i < B.size(); i++)
			if (B.get(i).getLastPostTime().compareTo(A.get(A.size() - 1).getLastPostTime()) < 0)
			//if (B.get(i).getLastPostTime() > A.get(A.size() - 1).getLastPostTime())
				break;
		A.addAll(B.subList(i, B.size()));
		return A.size();
	}

	/*public void loadMore(final int page) {
		CookieUtils.saveCookie(client, mContext);
        //try {
		//	String query = "?search=%23第2页";
		//String query = "?search=" + URLEncoder.encode("#第", "UTF-8") + "%202%20" + URLEncoder.encode("页", "UTF-8");
			String query = "?page=" + page;
		final String url = Constants.conversationListURL + channel + query;
            client.get(mContext, url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    ConversationListResult result = JSON.parseObject(response, ConversationListResult.class);
                    List<Conversation> newConversationList = result.getResults();
					List<Conversation> conversationList = mAdapter.getConversationList();
					int index = expandUnique(conversationList, newConversationList);
                    mAdapter.setConversationList(conversationList);
					mAdapter.notifyItemRangeInserted(index, conversationList.size());
                    //diffResult.dispatchUpdatesTo(mAdapter);
                    //l.smoothScrollToPosition(0);
                    swipyRefreshLayout.setRefreshing(false);
                    mPage = page;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_SHORT).show();
                    swipyRefreshLayout.setRefreshing(false);
                }
            });
	}*/
	private void getList(final int page)
	{
		Call<ConversationListResult> call = MyRetrofit.getService().listConversations(channel, page);
		call.enqueue(new Callback<ConversationListResult>() {
			@Override
			public void onResponse(Call<ConversationListResult> call, Response<ConversationListResult> response) {
				Log.d(TAG, "onResponse() called with: " + "call = [" + call + "], response = [" + response + "]");
				List<Conversation> conversationList = mAdapter.getConversationList();
				int oldLen = conversationList.size();
				List<Conversation> newConversationList = response.body().getResults();
				if (page == 1) {
					Log.d(TAG, "onResponse: " + mAdapter.getItemCount());
					DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(mAdapter.getConversationList(), newConversationList), true);
                    mAdapter.setConversationList(newConversationList);
                    diffResult.dispatchUpdatesTo(mAdapter);
                } else {
					expandUnique(conversationList, newConversationList);
					mAdapter.notifyItemRangeInserted(oldLen + 1, conversationList.size() - oldLen);
				}
				l.smoothScrollToPosition(page == 1 ? 0 : oldLen);
				swipyRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onFailure(Call<ConversationListResult> call, Throwable t) {
				Log.d(TAG, "onFailure() called with: " + "call = [" + call + "], t = [" + t + "]");
				t.printStackTrace();
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
			holder.avatarView.update(mContext, conversation.getStartMemberAvatarSuffix(),
					Integer.parseInt(conversation.getStartMemberId()), conversation.getStartMember());
			holder.avatarView.scale(20);
			holder.usernameTv.setText(conversation.getStartMember() + " 发表了帖子");
			holder.titleTv.setText(conversation.getTitle());
			holder.excerptTv.setText(conversation.getFirstPost());
			holder.channel.removeAllViews();
			holder.channel.addView(new ChannelTextView(mContext, Channel.getChannel(conversation.getChannelId())));
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
						jmp.setClass(mContext, PostActivity.class);
						mContext.startActivity(jmp);
					}
				});
				view.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						LinearLayout menuList = new LinearLayout(mContext);
						menuList.setPadding(Constants.paddingLeft, Constants.paddingTop,
								Constants.paddingRight, Constants.paddingBottom);

						AlertDialog.Builder menuBuilder = new AlertDialog.Builder(mContext).setView(menuList);
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
			Conversation oldConversation = oldConversationList.get(oldItemPosition);
			Conversation newConversation = newConversationList.get(newItemPosition);
			return !(oldConversation.getFirstPost() == null && newConversation.getFirstPost() != null)
					&& !(oldConversation.getFirstPost() != null && newConversation.getFirstPost() == null)
					&& ((oldConversation.getFirstPost() == null && newConversation.getFirstPost() == null) || oldConversation.getFirstPost().equals(newConversation.getFirstPost()))
					&& oldConversation.getReplies() == newConversation.getReplies();
		}

		@Override
		public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
			return oldConversationList.get(oldItemPosition).getConversationId() == newConversationList.get(newItemPosition).getConversationId();
		}
	}
}
