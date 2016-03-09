package com.geno.chaoli.forum;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;

/**
 * Created by jianhao on 16-3-4.
 */
public class TestActivity extends Activity implements LoginUtils.LoginObverser {
    LoginUtils loginUtils = new LoginUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginUtils.begin_login(this);
        Button button = (Button)findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUtils.login(getApplicationContext(), "我是大缺弦", "20141225qidai");
            }
        });
        Button button1 = (Button)findViewById(R.id.token_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUtils.getNewToken(getApplicationContext());
            }
        });
    }

    @Override
    public void onLoginFailure(int statusCode) {
        switch (statusCode){
            case LoginUtils.FAILED_AT_OPEN_LOGIN_PAGE:

                break;
            case LoginUtils.FAILED_AT_LOGIN:

                break;
            case LoginUtils.FAILED_AT_OPEN_HOMEPAGE:

                break;
            case LoginUtils.COOKIE_EXPIRED:
                CookieUtils.clearCookie(this);
                loginUtils.begin_login(this);
                break;
        }
    }

    @Override
    public void onLoginSuccess(int userId, String token) {
        Toast.makeText(this, "userId" + userId + ", token = " + token, Toast.LENGTH_SHORT).show();
    }
}
