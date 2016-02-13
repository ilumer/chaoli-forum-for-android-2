package com.geno.chaoli.forum.meta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

// TODO: 2016/2/12 0012 2226 Unable to debug this class... Now...
public class ConversationView extends View
{
	private static final String TAG = "ConversationView";

	public Conversation conversation;

	public ConversationView(final Context context, Conversation conversation)
	{
		this(context);
		this.conversation = conversation;
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO: 2016/2/12 0012 2123 Jump to post view.
			}
		});
		this.setOnLongClickListener(new OnLongClickListener() {
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
