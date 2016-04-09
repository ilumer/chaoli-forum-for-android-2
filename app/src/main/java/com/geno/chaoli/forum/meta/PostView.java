package com.geno.chaoli.forum.meta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.ReplyAction;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class PostView extends RelativeLayout
{
	public static final String TAG = "PostView";

	public AsyncHttpClient client;

	public Post post;
	public RelativeLayout avatar;
	public TextView username, signature, floor, content, time;

	public PostView(final Context context, final Post post)
	{
		this(context);
		View.inflate(context, R.layout.post_view, this);
		this.client = new AsyncHttpClient();
		CookieUtils.saveCookie(client, context);
		this.post = post;
		avatar = (RelativeLayout) findViewById(R.id.avatar);
		username = (TextView) findViewById(R.id.username);
		signature = (TextView) findViewById(R.id.signature);
		floor = (TextView) findViewById(R.id.floor);
		content = (TextView) findViewById(R.id.content);
		time = (TextView) findViewById(R.id.time);

		username.setText(post.username);
		floor.setText(String.format(Locale.getDefault(), "%d", post.getFloor()));


		if (post.deleteMemberId != 0)
		{
			this.setBackgroundColor(0xFF808080);
			avatar.setVisibility(GONE);
			signature.setVisibility(GONE);
			content.setVisibility(GONE);
		}
		avatar.addView(post.avatarView);
		signature.setText(post.signature);
		content.setText(post.getContent());
		time.setText(SimpleDateFormat.getDateTimeInstance().format(post.getTime() * 1000));

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

				ListView list = new ListView(context);
				list.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
						post.memberId == LoginUtils.getUserId() ? new String[]{context.getString(R.string.reply), context.getString(R.string.edit), context.getString(R.string.delete)} : new String[]{context.getString(R.string.reply)}
				));

				list.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						Intent toWriteSth = new Intent(context, ReplyAction.class);
						toWriteSth.putExtra("conversationId", post.conversationId);
						toWriteSth.putExtra("postId", post.postId);
						int actionFlag;
						switch (position)
						{
							case 0:
								actionFlag = ReplyAction.FLAG_REPLY;
								PostUtils.preQuote(context, post.postId);
								toWriteSth.putExtra("replyMsg", "[quote=" + post.postId + ":@" + post.username + "]" + post.content.replaceAll("\\[quote\\S+\\]", "").trim() + "[/quote]\n");
								break;
							case 1:
								actionFlag = ReplyAction.FLAG_EDIT;
								toWriteSth.putExtra("replyMsg", post.content);
								break;
							case 2:
								PostUtils.delete(context, post.postId, new PostUtils.DeleteObserver()
								{
									@Override
									public void onDeleteSuccess()
									{

									}

									@Override
									public void onDeleteFailure(int statusCode)
									{

									}
								});
								return;
							default:
								actionFlag = ReplyAction.FLAG_NORMAL;
						}
						toWriteSth.putExtra("flag", actionFlag);
						context.startActivity(toWriteSth);
					}
				});
				menu.addView(list);

				ab.setTitle("What do you want to do?").setView(menu);
				ab.show();
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
