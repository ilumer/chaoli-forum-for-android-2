package com.geno.chaoli.forum.meta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geno.chaoli.forum.PostActivity;
import com.geno.chaoli.forum.R;

import java.util.Locale;

public class ConversationView extends RelativeLayout
{
	private static final String TAG = "ConversationView";

	public Conversation conversation;

	public ConversationView(final Context context, final Conversation conversation)
	{
		this(context);
		View.inflate(context, R.layout.conversation_view, this);
		this.conversation = conversation;

		AvatarView avatarView = (AvatarView)findViewById(R.id.avatar);
		avatarView.update(context, conversation.getStartMemberAvatarSuffix(),
				Integer.parseInt(conversation.getStartMemberId()), conversation.getStartMember());
		avatarView.scale(20);
		((TextView) findViewById(R.id.username)).setText(conversation.getStartMember() + " 发表了帖子");
		//((TextView) findViewById(R.id.conversationId)).setText(String.format(Locale.getDefault(), "%d", conversation.getConversationId()));
		((TextView) findViewById(R.id.title)).setText(conversation.getTitle());
		String excerpt = conversation.getExcerpt();//.split("\\n")[0];  TextView有一个参数是可以自动把多出的字符变成...的(见xml文件),所以这个就不需要啦
		((TextView) findViewById(R.id.excerpt)).setText(excerpt);
		//((TextView) findViewById(R.id.excerpt)).setText(excerpt.length() > 50 ?
		//		excerpt.substring(0, 50) + "…" : excerpt);
//		((TextView) findViewById(R.id.replies)).setText(String.format(Locale.getDefault(), "%d", conversation.getReplies()));
		((LinearLayout) findViewById(R.id.channel)).addView(new ChannelTextView(context, conversation.getChannel()));
		((TextView) findViewById(R.id.reply_num)).setText(String.valueOf(conversation.getReplies()));

		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO: 2016/2/12 0012 2123 Jump to post view.
				//Toast.makeText(context, ((ConversationView) v).conversation.getConversationId() + "", Toast.LENGTH_SHORT).show();
				Intent jmp = new Intent();
				jmp.putExtra("conversationId", ((ConversationView) v).conversation.getConversationId());
				jmp.putExtra("title", ((ConversationView) v).conversation.getTitle());
				jmp.setClass(context, PostActivity.class);
				context.startActivity(jmp);
			}
		});

		this.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				// TODO: 2016/2/12 0012 2131 MarkAsRead and Star.
				LinearLayout menuList = new LinearLayout(context);
				menuList.setPadding(Constants.paddingLeft, Constants.paddingTop,
						Constants.paddingRight, Constants.paddingBottom);

				AlertDialog.Builder menuBuilder = new AlertDialog.Builder(context).setView(menuList);
				final Dialog menu = menuBuilder.create();
				return true;
			}
		});
	}

	public ConversationView(Context context)
	{
		this(context, (AttributeSet)null);
	}

	public ConversationView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ConversationView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
}
