package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.geno.chaoli.forum.meta.CookieUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jianhao on 16-4-7.
 */
public class SignUpActivity extends Activity {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this;
        final EditText username_edtTxt = (EditText)findViewById(R.id.edtTxt_username);
        EditText password_edtTxt = (EditText)findViewById(R.id.edtTxt_password);
        EditText retype_password_edtTxt = (EditText)findViewById(R.id.edtTxt_retype_password);
        final EditText email_edtTxt = (EditText)findViewById(R.id.edtTxt_email);
        final ImageView captcha_iv = (ImageView)findViewById(R.id.iv_captcha);
        final EditText captcha_edtTxt = (EditText)findViewById(R.id.edtTxt_captcha);
        Button sign_up_btn = (Button)findViewById(R.id.btn_sign_up);
        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        CookieUtils.saveCookie(client, this);

        final String signUpUrl = "https://chaoli.club/index.php/user/join?invite=14600222636921";
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
                    CookieUtils.saveCookie(client, mContext);
                    client.get("https://chaoli.club/index.php/mscaptcha", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            //BitmapFactory bitmapFactory = new BitmapFactory();
                            //工厂对象的decodeByteArray把字节转换成Bitmap对象
                            Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                            //设置图片
                            captcha_iv.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("username", "cdfaefddccc");
                params.put("email", "373331853@qq.com");
                params.put("password", "1234567");
                params.put("confirm", "1234567");
                params.put("mscaptcha", captcha_edtTxt.getText().toString());
                params.put("submit", "注册");

                CookieUtils.saveCookie(client, mContext);
                client.post(signUpUrl, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i("sign up", new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("sign up", new String(responseBody));
                    }
                });
            }
        });
    }
}
