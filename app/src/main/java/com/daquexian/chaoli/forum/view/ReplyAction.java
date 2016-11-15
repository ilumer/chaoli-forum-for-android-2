package com.daquexian.chaoli.forum.view;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.databinding.ReplyActionBinding;
import com.daquexian.chaoli.forum.meta.SFXParser3;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;
import com.daquexian.chaoli.forum.viewmodel.ReplyActionVM;


public class ReplyAction extends BaseActivity
{
	public static final String TAG = "ReplyAction";

	public static final int FLAG_NORMAL = 0;
	public static final int FLAG_REPLY = 1;
	public static final int FLAG_EDIT = 2;

	private static final int MENU_REPLY = 1;
	private static final int MENU_DEMO = 2;

	private ReplyActionVM viewModel;
	private ReplyActionBinding binding;

	/* 切换至演示模式时保存光标位置，切换回普通模式时恢复 */
	private int selectionStart, selectionEnd;

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
		setViewModel(new ReplyActionVM(flag, conversationId, postId, replyTo, replyMsg));
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
				viewModel.doAfterContentChanged();
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

		viewModel.updateRichText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				int selectionStart = binding.replyText.getSelectionStart();
				int selectionEnd = binding.replyText.getSelectionEnd();
				binding.replyText.setText(SFXParser3.parse(getApplicationContext(), viewModel.content.get(), null));
				binding.replyText.setSelection(selectionStart, selectionEnd);
			}
		});

		viewModel.demoMode.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				if (((ObservableBoolean) observable).get()) {
					selectionStart = binding.replyText.getSelectionStart();
					selectionEnd = binding.replyText.getSelectionEnd();
					binding.replyText.setEnabled(false);
					binding.replyText.setOnlineImgEnabled(true);
					binding.replyText.update();
				} else {
					binding.replyText.setEnabled(true);
					binding.replyText.setOnlineImgEnabled(false);
					binding.replyText.update();
					binding.replyText.setSelection(selectionStart, selectionEnd);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.NONE, MENU_DEMO, R.string.post).setIcon(R.drawable.ic_functions_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(Menu.NONE, Menu.NONE, MENU_REPLY, R.string.reply).setIcon(R.drawable.ic_cab_done_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getOrder())
		{
			case MENU_REPLY:
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
			case MENU_DEMO:
				viewModel.changeDemoMode();
				break;
		}
		return true;
	}

	@Override
	public void setViewModel(BaseViewModel viewModel) {
		this.viewModel = (ReplyActionVM) viewModel;
		binding = DataBindingUtil.setContentView(this, R.layout.reply_action);
		binding.setViewModel(this.viewModel);
	}
}
