package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.LoginUtils;

import java.lang.ref.WeakReference;

public class LoginActivity extends Activity
{
	public static final String TAG = "LoginActivity";

	private final Context mContext = this;

	public EditText loginName, loginPwd;
	public Button loginBtn, signUpBtn, answerQuestionBtn;
	public SharedPreferences sp;
	public SharedPreferences.Editor e;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		loginName = (EditText) findViewById(R.id.loginName);
		loginPwd = (EditText) findViewById(R.id.loginPwd);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		signUpBtn = (Button) findViewById(R.id.signUpBtn);
		answerQuestionBtn = (Button) findViewById(R.id.answerQuestionBtn);
		sp = getSharedPreferences(Constants.conversationSP, MODE_PRIVATE);
		e = sp.edit();

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.loginBtn:
						String name = loginName.getText().toString();
						String pwd = loginPwd.getText().toString();
						if (name.isEmpty() || pwd.isEmpty()) {
							Toast.makeText(LoginActivity.this,
									(name.isEmpty() ? getString(R.string.username)
											+ (pwd.isEmpty() ? " " + getString(R.string.and_password) : "") : getString(R.string.password))
											+ " " + getString(R.string.should_not_be_null), Toast.LENGTH_SHORT).show();
						} else {
							final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.just_a_sec));
							LoginUtils.LoginObserver observer = new LoginUtils.LoginObserver() {
								@Override
								public void onLoginSuccess(int userId, String token) {
									progressDialog.dismiss();
									e.putBoolean(Constants.loginBool, true);
									e.apply();
									//Toast.makeText(LoginActivity.this, userId + ", " + token, Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(LoginActivity.this, MainActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								}

								@Override
								public void onLoginFailure(int statusCode) {
									progressDialog.dismiss();
									e.putBoolean(Constants.loginBool, false);
									e.apply();
									switch (statusCode) {
										case LoginUtils.FAILED_AT_OPEN_LOGIN_PAGE:
											Toast.makeText(LoginActivity.this, R.string.network_err_open_login_page, Toast.LENGTH_SHORT).show();
											break;
										case LoginUtils.FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE:
											Toast.makeText(LoginActivity.this, R.string.network_err_get_token, Toast.LENGTH_SHORT).show();
											break;
										case LoginUtils.FAILED_AT_LOGIN:
											Toast.makeText(LoginActivity.this, R.string.network_err_login, Toast.LENGTH_SHORT).show();
											break;
										case LoginUtils.WRONG_USERNAME_OR_PASSWORD:
											Toast.makeText(LoginActivity.this, R.string.login_err_wrong_name_pwd, Toast.LENGTH_SHORT).show();
											break;
										case LoginUtils.FAILED_AT_OPEN_HOMEPAGE:
											Toast.makeText(LoginActivity.this, R.string.network_err_homepage, Toast.LENGTH_SHORT).show();
											break;
										case LoginUtils.COOKIE_EXPIRED:
											Toast.makeText(LoginActivity.this, R.string.login_err_cookie_expire, Toast.LENGTH_SHORT).show();
											break;
										case LoginUtils.EMPTY_UN_OR_PW:
											Toast.makeText(LoginActivity.this, R.string.login_err_empty, Toast.LENGTH_SHORT).show();
											break;
									}
									LoginUtils.clear(getApplicationContext());
								}
							};
							LoginUtils.begin_login(LoginActivity.this, name, pwd, observer);
						}
						break;
					case R.id.signUpBtn:
						final EditText inviteCodeET = new EditText(mContext);
						new AlertDialog.Builder(mContext).setTitle(R.string.please_enter_your_invite_code).setView(inviteCodeET)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(mContext, SignUpActivity.class);
								intent.putExtra("inviteCode", inviteCodeET.getText().toString());
								startActivity(intent);
							}
						}).setNegativeButton("取消", null).show();
						break;
					case R.id.answerQuestionBtn:
						startActivity(new Intent(mContext, AnswerQuestionsActivity.class));
						break;
				}
			}
		};
		loginBtn.setOnClickListener(onClickListener);
		answerQuestionBtn.setOnClickListener(onClickListener);
		signUpBtn.setOnClickListener(onClickListener);
	}
}
