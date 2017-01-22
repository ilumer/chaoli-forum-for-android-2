package com.daquexian.chaoli.forum.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.databinding.LoginActivityBinding;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;
import com.daquexian.chaoli.forum.viewmodel.LoginActivityVM;

public class LoginActivity extends BaseActivity
{
	public static final String TAG = "LoginActivity";

	Context mContext = this;
	LoginActivityVM viewModel;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setViewModel(new LoginActivityVM());

		viewModel.clickAQ.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				startActivity(new Intent(LoginActivity.this, AnswerQuestionsActivity.class));
			}
		});

		viewModel.showToast.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				showToast(viewModel.toastContent);
			}
		});

		viewModel.goToMainActivity.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				/* Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent); */
				setResult(RESULT_OK);
				finish();
			}
		});

		viewModel.showProgressDialog.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				if (((ObservableBoolean) observable).get()) {
					progressDialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.just_a_sec));
					progressDialog.show();
				} else {
					if (progressDialog != null) progressDialog.dismiss();
				}
			}
		});

		viewModel.clickSignUp.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				final EditText inviteCodeET = new EditText(mContext);
				new AlertDialog.Builder(mContext).setTitle(R.string.please_enter_your_invite_code).setView(inviteCodeET)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(mContext, SignUpActivity.class);
								intent.putExtra("inviteCode", inviteCodeET.getText().toString());
								mContext.startActivity(intent);
							}
						}).setNegativeButton("取消", null).show();
			}
		});
	}

	@Override
	public void setViewModel(BaseViewModel viewModel) {
		this.viewModel = (LoginActivityVM) viewModel;
		LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
		binding.setViewModel(this.viewModel);
	}
}
