package com.geno.chaoli.forum.meta;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.R;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostView extends RelativeLayout
{
	public static final String TAG = "PostView";

	public Post post;

	public PostView(final Context context, final Post post)
	{
		this(context);
		View.inflate(context, R.layout.post_view, this);
		this.post = post;

		((RelativeLayout) findViewById(R.id.avatar)).addView(post.avatarView);
		((TextView) findViewById(R.id.username)).setText(post.getUsername());
		((TextView) findViewById(R.id.signature)).setText(post.preferences.getSignature());
		((TextView) findViewById(R.id.floor)).setText(String.format(Locale.getDefault(), "%d", post.getFloor()));
		((TextView) findViewById(R.id.content)).setText(post.getContent());
		((TextView) findViewById(R.id.time)).setText(SimpleDateFormat.getDateInstance().format(post.getTime() * 1000));

		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO: 16-3-3 0804 Reply
				Toast.makeText(context, post.floor + " get click", Toast.LENGTH_SHORT).show();
			}
		});

		this.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				// TODO: 16-3-3 0805 Reply.
				AlertDialog.Builder ab = new AlertDialog.Builder(context);

				LinearLayout menu = new LinearLayout(context);

				TextView title = new TextView(context);
				title.setTextSize(20);
				title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
				menu.addView(title);

				TextView replyTV = new TextView(context);
				replyTV.setText(context.getString(R.string.reply));
				replyTV.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

					}
				});

				ab.setTitle("What do you want to do?").setView(menu);
				//Toast.makeText(context, post.floor + " get long click", Toast.LENGTH_SHORT).show();
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
