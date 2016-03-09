package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.Methods;

import java.lang.ref.WeakReference;

public class LoginActivity extends Activity
{
	public EditText loginName, loginPwd;
	public Button loginBtn;
	public SharedPreferences sp;
	public SharedPreferences.Editor e;

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
		loginBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String name = loginName.getText().toString();
				String pwd = loginPwd.getText().toString();
				Methods.login(LoginActivity.this, name, pwd);
			}
		});
	}
}
