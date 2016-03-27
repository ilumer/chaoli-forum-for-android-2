package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;

public class ReplyAction extends AppCompatActivity
{
	public int conversationId;

	public AsyncHttpClient client;

	public String replyMsg;

	public EditText replyText;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public static final int menu_draft = 0;
	public static final int menu_reply = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_action);
		setTitle(R.string.reply);
		client = new AsyncHttpClient();
		sp = getSharedPreferences("draftList", MODE_PRIVATE);
		e = sp.edit();
		Bundle data = getIntent().getExtras();
		conversationId = data.getInt("conversationId");
		replyMsg = data.getString("replyMsg", "");
		replyText = (EditText) findViewById(R.id.replyText);
		replyText.setText(replyMsg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, menu_draft, R.string.save_as_draft).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu.NONE, menu_reply, R.string.reply).setIcon(R.drawable.ic_cab_done_mtrl_alpha);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getOrder())
		{
			case menu_draft:
				e.putBoolean(conversationId + "", true);
				e.commit();
				getSharedPreferences(conversationId + "", MODE_PRIVATE).edit()
						.putString("replyText", replyText.getText().toString()).commit();
				finish();
				break;
			case menu_reply:

		}
		return true;
	}
}
