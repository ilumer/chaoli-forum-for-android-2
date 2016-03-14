package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.LoginUtils;
import com.geno.chaoli.forum.meta.Methods;

import java.lang.ref.WeakReference;

public class LoginActivity extends Activity
{
	public EditText loginName, loginPwd;
	public Button loginBtn;
	public SharedPreferences sp;
	public SharedPreferences.Editor e;

	public LoginUtils.LoginObserver observer;

	public static class LoginHandler extends Handler
	{
		WeakReference<Activity> activity;

		public LoginHandler(Activity activity)
		{
			this.activity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case Constants.FINISH_LOGIN_LIST_ANALYSIS:
					activity.get().getSharedPreferences(Constants.conversationSP, MODE_PRIVATE).edit().putBoolean(Constants.loginBool, true).apply();
					((MainActivity) new Activity()).mainHandler.sendEmptyMessage(Constants.FINISH_LOGIN);

			}
		}
	}

	public LoginHandler loginHandler = new LoginHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		loginName = (EditText) findViewById(R.id.loginName);
		loginPwd = (EditText) findViewById(R.id.loginPwd);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		observer = new LoginUtils.LoginObserver()
		{
			@Override
			public void onLoginSuccess(int userId, String token)
			{

			}

			@Override
			public void onLoginFailure(int statusCode)
			{
				switch (statusCode)
				{
					case LoginUtils.FAILED_AT_OPEN_LOGIN_PAGE:
						Toast.makeText(LoginActivity.this, "FAILED_AT_OEPN_LOGIN_PAGE", Toast.LENGTH_SHORT).show();
						break;
					case LoginUtils.FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE:
						Toast.makeText(LoginActivity.this, "FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE", Toast.LENGTH_SHORT).show();
						break;
					case LoginUtils.FAILED_AT_LOGIN:
						Toast.makeText(LoginActivity.this, "FAILED_AT_LOGIN", Toast.LENGTH_SHORT).show();
						break;
					case LoginUtils.WRONG_USERNAME_OR_PASSWORD:
						Toast.makeText(LoginActivity.this, "WRONG_USERNAME_OR_PASSWORD", Toast.LENGTH_SHORT).show();
						break;
					case LoginUtils.FAILED_AT_OPEN_HOMEPAGE:
						Toast.makeText(LoginActivity.this, "FAILED_AT_OPEN_HOMEPAGE", Toast.LENGTH_SHORT).show();
						break;
					case LoginUtils.COOKIE_EXPIRED:
						Toast.makeText(LoginActivity.this, "COOKIE_EXPIRED", Toast.LENGTH_SHORT).show();
						break;
					case LoginUtils.EMPTY_UN_OR_PW:
						Toast.makeText(LoginActivity.this, "EMPTY_UN_OR_PW", Toast.LENGTH_SHORT).show();
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
				LoginUtils.begin_login(LoginActivity.this, name, pwd, observer);

			}
		});
	}
}
