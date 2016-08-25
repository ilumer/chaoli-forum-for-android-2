package com.geno.chaoli.forum.app;

import android.app.Application;

import com.geno.chaoli.forum.meta.Constants;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by jianhao on 16-8-25.
 */
public class ChaoliApplication extends Application {
    Retrofit retrofit;
    OkHttpClient okHttpClient;
    @Override
    public void onCreate() {
        super.onCreate();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return null;
                    }
                }).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .build();
    }
}
