package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jianhao on 16-4-7.
 * SignUpActivity
 */

public class SignUpActivity extends Activity {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this;

        final EditText username_edtTxt = (EditText)findViewById(R.id.edtTxt_username);
        final EditText password_edtTxt = (EditText)findViewById(R.id.edtTxt_password);
        final EditText retype_password_edtTxt = (EditText)findViewById(R.id.edtTxt_retype_password);
        final EditText email_edtTxt = (EditText)findViewById(R.id.edtTxt_email);
        final ImageView captcha_iv = (ImageView)findViewById(R.id.iv_captcha);
        final EditText captcha_edtTxt = (EditText)findViewById(R.id.edtTxt_captcha);
        Button refresh_captcha_btn = (Button) findViewById(R.id.btn_refresh_captcha);
        Button sign_up_btn = (Button)findViewById(R.id.btn_sign_up);
        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        CookieUtils.clearCookie(this);
        CookieUtils.saveCookie(client, this);

        Bundle bundle = getIntent().getExtras();
        String inviteCode = bundle == null ? "" :bundle.getString("inviteCode", "");

        final String signUpUrl = Constants.SIGN_UP_URL + inviteCode;
        client.get(this, signUpUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.i("sign up", response);
                String tokenFormat = "\"token\":\"([\\dabcdef]+)";
                Pattern pattern = Pattern.compile(tokenFormat);
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {
                    Log.i("token", matcher.group(1));
                    params.put("token", matcher.group(1));
                    getAndShowCaptchaImage(client, captcha_iv);
                } else {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("sd", "sdf");
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_sign_up:
                        final String USERNAME_HAS_BEEN_USED = "用户名已经被注册了";
                        final String EMAIL_HAS_BEEN_USED = "邮箱已被注册";
                        final String WRONG_CAPTCHA = "你也许需要一个计算器";

                        final String username = username_edtTxt.getText().toString();
                        final String password = password_edtTxt.getText().toString();
                        String confirm = retype_password_edtTxt.getText().toString();
                        String email = email_edtTxt.getText().toString();

                        if (password.length() <= 6) {
                            ((TextView)((Activity)mContext).findViewById(R.id.tv_password_msg)).setText(R.string.at_least_six_character);
                            return;
                        }
                        if (!password.equals(confirm)) {
                            ((TextView)((Activity)mContext).findViewById(R.id.tv_retype_msg)).setText(R.string.should_be_same_with_password);
                            return;
                        }
                        if (!email.contains("@") || !email.contains(".")) {
                            ((TextView)((Activity)mContext).findViewById(R.id.tv_email_msg)).setText(R.string.invaild_email);
                            return;
                        }
                        params.put("username", username);
                        params.put("email", email);
                        params.put("password", password);
                        params.put("confirm", confirm);
                        params.put("mscaptcha", captcha_edtTxt.getText().toString());
                        params.put("submit", "注册");

                        CookieUtils.saveCookie(client, mContext);
                        client.post(signUpUrl, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String response = new String(responseBody);
                                if(response.contains(USERNAME_HAS_BEEN_USED)){
                                    ((TextView)((Activity)mContext).findViewById(R.id.tv_username_msg)).setText(R.string.username_has_been_used);
                                }else if(response.contains(EMAIL_HAS_BEEN_USED)){
                                    ((TextView)((Activity)mContext).findViewById(R.id.tv_email_msg)).setText(R.string.email_has_been_used);
                                } else if (response.contains(WRONG_CAPTCHA)) {
                                    ((TextView) ((Activity) mContext).findViewById(R.id.tv_captcha_msg)).setText(R.string.wrong_captcha);
                                } else {
                                    Toast.makeText(mContext, R.string.sign_up_error, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                if(statusCode == 302){  //登录成功
                                    LoginUtils.saveUsernameAndPassword(mContext, username, password);
                                    Toast.makeText(getApplicationContext(), R.string.sign_up_successfully, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.setClass(SignUpActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);   //清除所在栈所有Activity
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                    case R.id.btn_refresh_captcha:
                        getAndShowCaptchaImage(client, captcha_iv);
                        break;
                }
            }
        };
        refresh_captcha_btn.setOnClickListener(onClickListener);
        sign_up_btn.setOnClickListener(onClickListener);
    }

    private void getAndShowCaptchaImage(AsyncHttpClient client, final ImageView captcha_iv){
        CookieUtils.saveCookie(client, mContext);
        client.get(Constants.GET_CAPTCHA_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                //设置图片
                captcha_iv.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(mContext, R.string.error_when_retrieving_captcha, Toast.LENGTH_LONG).show();
            }
        });
    }
}
