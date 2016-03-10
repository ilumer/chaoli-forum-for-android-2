package com.geno.chaoli.forum;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;

/**
 * Created by jianhao on 16-3-4.
 */
public class TestActivity extends Activity implements LoginUtils.LoginObverser, LoginUtils.LogoutObverser {
    final String USERNAME = "我是大缺弦";
    final String PASSWORD = "20141225qidai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        LoginUtils.begin_login(this, USERNAME, PASSWORD, this);
    }

    @Override
    public void onLoginFailure(int statusCode) {
        Log.e("LoginFailure", String.valueOf(statusCode));
        switch (statusCode){
            case LoginUtils.FAILED_AT_OPEN_LOGIN_PAGE:

                break;
            case LoginUtils.FAILED_AT_LOGIN:

                break;
            case LoginUtils.FAILED_AT_OPEN_HOMEPAGE:

                break;
            case LoginUtils.COOKIE_EXPIRED:
                Log.i("LoginUtils", "cookie_expired");
                CookieUtils.clearCookie(this);
                LoginUtils.begin_login(this, this);
                break;
            case LoginUtils.EMPTY_UN_OR_PW:
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                LoginUtils.begin_login(this, USERNAME, PASSWORD, this);
                break;
        }
    }

    @Override
    public void onLoginSuccess(int userId, String token) {
        Toast.makeText(this, "userId" + userId + ", token = " + token, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogoutFailure(int statusCode) {
        Log.e("LogoutFailure", String.valueOf(statusCode));
    }

    @Override
    public void onLogoutSuccess() {

    }
}
