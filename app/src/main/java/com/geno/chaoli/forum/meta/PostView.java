package com.geno.chaoli.forum.meta;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geno.chaoli.forum.R;

public class PostView extends RelativeLayout
{
	public static final String TAG = "PostView";

	public Post post;

	public PostView(final Context context, Post post)
	{
		this(context);
		View.inflate(context, R.layout.postview, this);
		this.post = post;
		((TextView) findViewById(R.id.username)).setText(post.getUsername());
		((TextView) findViewById(R.id.floor)).setText(post.getFloor() + "");
		((TextView) findViewById(R.id.time)).setText(post.getTime() + "");
		((TextView) findViewById(R.id.content)).setText(post.getContent());

		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO: 16-3-3 0804 Reply
			}
		});

		this.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				// TODO: 16-3-3 0805 Reply.
				AlertDialog.Builder ab = new AlertDialog.Builder(context);
				return true;
			}
		});
	}

	public PostView(Context context)
	{
		this(context, (AttributeSet) null);
	}

	public PostView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public PostView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
}
