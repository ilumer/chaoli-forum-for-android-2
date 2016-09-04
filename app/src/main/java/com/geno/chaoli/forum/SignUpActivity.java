package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.network.MyOkHttp;
import com.geno.chaoli.forum.utils.LoginUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jianhao on 16-4-7.
 * SignUpActivity
 */

public class SignUpActivity extends BaseActivity {
    final static String TAG = "SignUpActivity";

    Context mContext;
    String mToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this;
        configToolbar(R.string.sign_up);

        final EditText username_edtTxt = (EditText)findViewById(R.id.edtTxt_username);
        final EditText password_edtTxt = (EditText)findViewById(R.id.edtTxt_password);
        final EditText retype_password_edtTxt = (EditText)findViewById(R.id.edtTxt_retype_password);
        final EditText email_edtTxt = (EditText)findViewById(R.id.edtTxt_email);
        final ImageView captcha_iv = (ImageView)findViewById(R.id.iv_captcha);
        final EditText captcha_edtTxt = (EditText)findViewById(R.id.edtTxt_captcha);
        Button refresh_captcha_btn = (Button) findViewById(R.id.btn_refresh_captcha);
        Button sign_up_btn = (Button)findViewById(R.id.btn_sign_up);

        Bundle bundle = getIntent().getExtras();
        String inviteCode = bundle == null ? "" :bundle.getString("inviteCode", "");

        if("".equals(inviteCode)){
            Toast.makeText(getApplicationContext(), R.string.you_can_only_sign_up_with_an_invite_code, Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(mContext, inviteCode, Toast.LENGTH_LONG).show();
        }

        final String signUpUrl = Constants.SIGN_UP_URL + inviteCode;
        MyOkHttp.clearCookie();
        new MyOkHttp.MyOkHttpClient()
                .get(signUpUrl)
                .enqueue(mContext, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() != 200) Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                        else {
                            String responseStr = response.body().string();
                            response.body().close();
                            String tokenFormat = "\"token\":\"([\\dabcdef]+)";
                            Pattern pattern = Pattern.compile(tokenFormat);
                            Matcher matcher = pattern.matcher(responseStr);
                            if (matcher.find()) {
                                Log.i("token", matcher.group(1));

                                //params.put("token", matcher.group(1));
                                mToken = matcher.group(1);
                                getAndShowCaptchaImage(captcha_iv);
                            } else {
                                Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                            }
                        }
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

                        TextInputLayout passwordTIL = (TextInputLayout) ((Activity) mContext).findViewById(R.id.passwordTIL);
                        TextInputLayout retypePasswordTIL = (TextInputLayout) ((Activity) mContext).findViewById(R.id.retypePasswordTIL);
                        final TextInputLayout usernameTIL = (TextInputLayout) ((Activity) mContext).findViewById(R.id.usernameTIL);
                        final TextInputLayout emailTIL = (TextInputLayout) ((Activity) mContext).findViewById(R.id.emailTIL);
                        final TextInputLayout captchaTIL = (TextInputLayout) ((Activity) mContext).findViewById(R.id.captchaTIL);

                        Boolean flagError = false;

                        passwordTIL.setError("");
                        retypePasswordTIL.setError("");
                        usernameTIL.setError("");
                        emailTIL.setError("");
                        captchaTIL.setError("");

                        if (username.length() < 4 || username.length() > 21) {
                            usernameTIL.setError(getString(R.string.length_of_username_should_be_between_4_and_21));
                            flagError = true;
                        }
                        if (password.length() < 6) {
                            passwordTIL.setError(getString(R.string.at_least_six_character));
                            flagError = true;
                        }
                        if (!password.equals(confirm)) {
                            retypePasswordTIL.setError(getString(R.string.should_be_same_with_password));
                            flagError = true;
                        }
                        if (!email.contains("@") || !email.contains(".")) {
                            emailTIL.setError(getString(R.string.invaild_email));
                            flagError = true;
                        }
                        if (flagError) return;

                        MyOkHttp.MyOkHttpClient myOkHttpClient = new MyOkHttp.MyOkHttpClient()
                                .add("username", username)
                                .add("email", email)
                                .add("password", password)
                                .add("confirm", confirm)
                                .add("mscaptcha", captcha_edtTxt.getText().toString())
                                .add("token", mToken)
                                .add("submit", "注册");

                        final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.just_a_sec));
                        progressDialog.show();
                        myOkHttpClient.post(signUpUrl)
                                .enqueue(mContext, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                                        getAndShowCaptchaImage(captcha_iv);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.code() != 200){
                                            Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                                            getAndShowCaptchaImage(captcha_iv);
                                            progressDialog.dismiss();
                                        }
                                        else {
                                            String responseStr = response.body().string();
                                            response.body().close();
                                            if(responseStr.contains(USERNAME_HAS_BEEN_USED)){
                                                usernameTIL.setError(getString(R.string.username_has_been_used));
                                            }else if(responseStr.contains(EMAIL_HAS_BEEN_USED)){
                                                emailTIL.setError(getString(R.string.email_has_been_used));
                                            } else if (responseStr.contains(WRONG_CAPTCHA)) {
                                                captchaTIL.setError(getString(R.string.wrong_captcha));
                                            } else {
                                                //Toast.makeText(mContext, R.string.sign_up_error, Toast.LENGTH_LONG).show();
                                                Log.d(TAG, "onResponse: " + responseStr);
                                                LoginUtils.saveUsernameAndPassword(mContext, username, password);
                                                Toast.makeText(getApplicationContext(), R.string.sign_up_successfully, Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent();
                                                intent.setClass(SignUpActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);   //清除所在栈所有Activity
                                                startActivity(intent);
                                            }
                                            progressDialog.dismiss();
                                        }

                                    }
                                });
                        /*client.post(signUpUrl, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                progressDialog.dismiss();
                                if(statusCode == 302){  //登录成功
                                    LoginUtils.saveUsernameAndPassword(mContext, username, password);
                                    Toast.makeText(getApplicationContext(), R.string.sign_up_successfully, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.setClass(SignUpActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);   //清除所在栈所有Activity
                                    startActivity(intent);
                                }
                            }
                        });*/
                        break;
                    case R.id.btn_refresh_captcha:
                        getAndShowCaptchaImage(captcha_iv);
                        break;
                }
            }
        };
        refresh_captcha_btn.setOnClickListener(onClickListener);
        sign_up_btn.setOnClickListener(onClickListener);
    }

    private void getAndShowCaptchaImage(final ImageView captcha_iv){
        Log.d(TAG, "getAndShowCaptchaImage() called with: " + "captcha_iv = [" + captcha_iv + "]");
        captcha_iv.setImageDrawable(getResources().getDrawable(R.drawable.refreshing));
        //OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(MyOkHttp.getClient());
        //Glide.get(this).register(GlideUrl.class, InputStream.class, factory);
        //Glide.with(this).load(Constants.GET_CAPTCHA_URL).into(captcha_iv);
        new MyOkHttp.MyOkHttpClient()
                .get(Constants.GET_CAPTCHA_URL)
                .enqueue(mContext, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        byte[] bytes = response.body().bytes();
                        captcha_iv.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                });
        /*client.get(Constants.GET_CAPTCHA_URL, new AsyncHttpResponseHandler() {
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
        });*/
    }
}
