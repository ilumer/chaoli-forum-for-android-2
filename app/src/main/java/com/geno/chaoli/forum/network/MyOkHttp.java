package com.geno.chaoli.forum.network;

import android.content.Context;
import android.os.Handler;

import com.geno.chaoli.forum.app.ChaoliApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jianhao on 16-9-3.
 * A OkHttp Wrapper
 */
public class MyOkHttp {
    private final static String TAG = "MyOkHttp";
    private static OkHttpClient okHttpClient;
    private static CookiesManager mCookiesManager;
    //private static HashMap<String, List<Cookie>> mCookieStore = new HashMap<>();
    private static Context mContext = ChaoliApplication.getAppContext();

    public synchronized static OkHttpClient getClient(){
        if (okHttpClient == null) {
            //mContext = context;
            mCookiesManager = new CookiesManager();
            okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(mCookiesManager)
                    /*.cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            mCookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = mCookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })*/
                    //.addInterceptor(new ReceivedCookiesInterceptor(context))
                    //.addInterceptor(new AddCookiesInterceptor(context))
                    .build();
        }
        return okHttpClient;
    }

    public synchronized static void clearCookie(){
        mCookiesManager.clear();
    }

    public static class MyOkHttpClient {
        private MultipartBody.Builder builder;
        //private FormBody.Builder formBuilder;
        private RequestBody requestBody;
        private Request.Builder requestBuilder;
        private Request request;

        public MyOkHttpClient get(String url){
            requestBuilder = new Request.Builder().get();
            requestBuilder = requestBuilder.url(url);
            return this;
        }
        public MyOkHttpClient post(String url){
            requestBody = builder.build();
            requestBuilder = new Request.Builder().post(requestBody);
            requestBuilder = requestBuilder.url(url);
            return this;
        }
        public MyOkHttpClient add(String key, String value) {
            if (builder == null) builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addFormDataPart(key, value);
            //if (formBuilder == null) formBuilder = new FormBody.Builder();
            //formBuilder.add(key, value);
            return this;
        }

        public MyOkHttpClient add(String key, String type, File file) {
            if (builder == null) builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(type), file));
            return this;
        }
        /*public MyOkHttpClient url(String url) {
            requestBuilder = requestBuilder.url(url);
            return this;
        }*/

        public void enqueue(final Context context, final Callback callback) {
            request = requestBuilder.build();
            Call call = getClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(call, e);
                        }
                    });
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callback.onResponse(call, response);
                            } catch (IOException e) {
                                onFailure(call, e);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * https://segmentfault.com/a/1190000004345545
     */
    private static class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(mContext);

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            return cookieStore.get(url);
        }

        public void clear(){
            cookieStore.removeAll();
        }
    }
}
