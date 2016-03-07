package com.geno.chaoli.forum;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.geno.chaoli.forum.meta.Methods;

public class LoginActivity extends Activity
{
	public EditText loginName, loginPwd;
	public Button loginBtn;
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
				Methods.login(name, pwd);
			}
		});
	}
}
