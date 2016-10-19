package com.geno.chaoli.forum.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.databinding.LoginActivityBinding;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.utils.LoginUtils;
import com.geno.chaoli.forum.viewmodel.BaseViewModel;
import com.geno.chaoli.forum.viewmodel.LoginActivityViewModel;

public class LoginActivity extends BaseActivity
{
	public static final String TAG = "LoginActivity";

	Context mContext = this;
	LoginActivityViewModel viewModel;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setViewModel(new LoginActivityViewModel());

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
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
		this.viewModel = (LoginActivityViewModel) viewModel;
		LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
		binding.setViewModel(this.viewModel);
	}
}
