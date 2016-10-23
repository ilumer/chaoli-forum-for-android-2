package com.geno.chaoli.forum.view;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.databinding.ReplyActionBinding;
import com.geno.chaoli.forum.utils.PostUtils;
import com.geno.chaoli.forum.viewmodel.BaseViewModel;
import com.geno.chaoli.forum.viewmodel.ReplyActionViewModel;

import java.util.Locale;


public class ReplyAction extends BaseActivity
{
	public static final String TAG = "ReplyAction";

	public static final int FLAG_NORMAL = 0;
	public static final int FLAG_REPLY = 1;
	public static final int FLAG_EDIT = 2;

	public static final int menu_reply = 1;

	ReplyActionViewModel viewModel;
	ReplyActionBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		int flag;
		int conversationId, postId;
		String replyTo;
		String replyMsg;
		Bundle data = getIntent().getExtras();
		flag = data.getInt("flag");
		conversationId = data.getInt("conversationId");
		postId = data.getInt("postId", -1);
		replyTo = data.getString("replyTo", "");
		replyMsg = data.getString("replyMsg", "");

		//setContentView(R.layout.reply_action);
		setViewModel(new ReplyActionViewModel(flag, conversationId, postId, replyTo, replyMsg));
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

		binding.replyText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				viewModel.saveReply();
			}
		});

		viewModel.replyComplete.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				setResult(RESULT_OK);
				finish();
			}
		});

		viewModel.editComplete.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				finish();
			}
		});

		viewModel.showToast.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showToast(viewModel.toastContent.get());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, menu_reply, R.string.reply).setIcon(R.drawable.ic_cab_done_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getOrder())
		{
			case menu_reply:
				switch (viewModel.flag.get())
				{
					case FLAG_NORMAL:
						viewModel.reply();
						break;
					case FLAG_EDIT:
						viewModel.edit();
						break;
				}
				break;
		}
		return true;
	}

	@Override
	public void setViewModel(BaseViewModel viewModel) {
		this.viewModel = (ReplyActionViewModel) viewModel;
		binding = DataBindingUtil.setContentView(this, R.layout.reply_action);
		binding.setViewModel(this.viewModel);
	}
}
