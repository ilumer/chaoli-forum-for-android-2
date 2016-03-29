package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	public EditText loginName, loginPwd;
	public Button loginBtn;
	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public LoginUtils.LoginObserver observer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		loginName = (EditText) findViewById(R.id.loginName);
		loginPwd = (EditText) findViewById(R.id.loginPwd);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		sp = getSharedPreferences(Constants.conversationSP, MODE_PRIVATE);
		e = sp.edit();
		observer = new LoginUtils.LoginObserver()
		{
			@Override
			public void onLoginSuccess(int userId, String token)
			{
				e.putBoolean(Constants.loginBool, true);
				e.apply();
				Toast.makeText(LoginActivity.this, userId + ", " + token, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLoginFailure(int statusCode)
			{
				e.putBoolean(Constants.loginBool, false);
				e.apply();
				switch (statusCode)
				{
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
		loginBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String name = loginName.getText().toString();
				String pwd = loginPwd.getText().toString();
				if (name.isEmpty() || pwd.isEmpty())
				{
					Toast.makeText(LoginActivity.this,
							(name.isEmpty() ? getString(R.string.username)
											  + (pwd.isEmpty() ? " " + getString(R.string.and_password) : "") : getString(R.string.password))
							+ " " + getString(R.string.should_not_be_null), Toast.LENGTH_SHORT).show();
				}
				else
					LoginUtils.begin_login(LoginActivity.this, name, pwd, observer);
			}
		});
	}
}
