package com.geno.chaoli.forum;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.geno.chaoli.forum.utils.PostUtils;

import java.util.Locale;


public class ReplyAction extends AppCompatActivity
{
	public static final String TAG = "ReplyAction";

	public static final int FLAG_NORMAL = 0;
	public static final int FLAG_REPLY = 1;
	public static final int FLAG_EDIT = 2;

	public int flag;

	public int conversationId, postId;
	public String replyTo;

	public String replyMsg;

	public EditText replyText;

	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	//public static final int menu_draft = 0;
	public static final int menu_reply = 1;
	//public static final int menu_purge = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_action);
		Toolbar toolbar = (Toolbar) findViewById(R.id.tl_custom);
		toolbar.setTitle(R.string.reply);
		toolbar.setTitleTextColor(getResources().getColor(R.color.white));
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		sp = getSharedPreferences(TAG, MODE_PRIVATE);
		e = sp.edit();
		Bundle data = getIntent().getExtras();
		flag = data.getInt("flag");
		conversationId = data.getInt("conversationId");
		postId = data.getInt("postId", -1);
		replyTo = data.getString("replyTo", "");
		replyMsg = data.getString("replyMsg", "");

		replyText = (EditText) findViewById(R.id.replyText);

		String draft = sp.getString(String.valueOf(conversationId), "");
		if (!"".equals(draft)) replyText.setText(draft);
        if (postId != -1) replyText.setText(String.format(Locale.ENGLISH, "[quote=%d:@%s]%s[/quote]\n", postId, replyTo, replyMsg));
		replyText.setSelection(replyText.getText().length());
		replyText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				e.putString(String.valueOf(conversationId), editable.toString()).apply();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		//menu.add(Menu.NONE, Menu.NONE, menu_draft, R.string.save_as_draft).setIcon(android.R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, Menu.NONE, menu_reply, R.string.reply).setIcon(R.drawable.ic_cab_done_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//menu.add(Menu.NONE, Menu.NONE, menu_purge, R.string.purge).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getOrder())
		{
			/*case menu_draft:
				e.putString(String.valueOf(conversationId), replyText.getText().toString()).commit();
				//e.putBoolean(conversationId + "", true).commit();
				//getSharedPreferences(conversationId + "", MODE_PRIVATE).edit()
				//		.putString("replyText", replyText.getText().toString()).commit();
				Toast.makeText(ReplyAction.this, R.string.save_as_draft, Toast.LENGTH_SHORT).show();
				finish();
				break;
				*/
			case menu_reply:
				switch (flag)
				{
					case FLAG_NORMAL:
						PostUtils.reply(ReplyAction.this, conversationId, replyText.getText().toString(), new PostUtils.ReplyObserver()
						{
							@Override
							public void onReplySuccess()
							{
								Toast.makeText(ReplyAction.this, R.string.reply, Toast.LENGTH_SHORT).show();
								e.putString(String.valueOf(conversationId), "").apply();
								setResult(RESULT_OK);
								finish();
							}

							@Override
							public void onReplyFailure(int statusCode)
							{
								Toast.makeText(ReplyAction.this, "Fail: " + statusCode, Toast.LENGTH_SHORT).show();
							}
						});
						break;
					/*case FLAG_REPLY:
						PostUtils.quote(ReplyAction.this, mConversationId, replyText.getText().toString(), new PostUtils.QuoteObserver()
						{
							@Override
							public void onQuoteSuccess()
							{
								finish();
							}

							@Override
							public void onQuoteFailure(int statusCode)
							{

							}
						});
						break;
						*/
					case FLAG_EDIT:
						PostUtils.edit(ReplyAction.this, postId, replyText.getText().toString(), new PostUtils.EditObserver()
						{
							@Override
							public void onEditSuccess()
							{
								Toast.makeText(ReplyAction.this, "Post", Toast.LENGTH_SHORT).show();
								finish();
							}

							@Override
							public void onEditFailure(int statusCode)
							{
								Toast.makeText(ReplyAction.this, "Fail: " + statusCode, Toast.LENGTH_SHORT).show();
							}
						});
				}
				break;
			/*case menu_purge:
				getSharedPreferences(conversationId + "", MODE_PRIVATE).edit().clear().apply();
				break;
				*/
		}
		return true;
	}
}
