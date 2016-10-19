package com.geno.chaoli.forum.network;

import android.content.Context;
import android.os.Handler;

import com.geno.chaoli.forum.ChaoliApplication;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
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
    private static Context mContext = ChaoliApplication.getAppContext();

    public synchronized static OkHttpClient getClient(){
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            try {
                // Create a trust manager that does not validate certificate chains
                final X509TrustManager trustAllCert =
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        };
                // Install the all-trusting trust manager
                final SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);
                builder.sslSocketFactory(sslSocketFactory, trustAllCert);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            mCookiesManager = new CookiesManager();
            okHttpClient = builder
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
            if (file != null) builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(type), file));
            return this;
        }
        /*public MyOkHttpClient url(String url) {
            requestBuilder = requestBuilder.url(url);
            return this;
        }*/

        public void enqueue(final Context context, final Callback callback) {
            request = requestBuilder.build();
            Call call = getClient().newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    new Handler(ChaoliApplication.getAppContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(call, e);
                        }
                    });
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    final String responseStr = response.body().string();
                    new Handler(ChaoliApplication.getAppContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callback.onResponse(call, response, responseStr);
                            } catch (IOException e) {
                                onFailure(call, e);
                            }
                        }
                    });
                    response.body().close();
                }
            });
        }

        public void enqueue(final Context context, final Callback1 callback) {
            request = requestBuilder.build();
            Call call = getClient().newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    new Handler(ChaoliApplication.getAppContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(call, e);
                        }
                    });
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    new Handler(ChaoliApplication.getAppContext().getMainLooper()).post(new Runnable() {
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
    public static abstract class Callback {
        public abstract void onFailure(Call call, IOException e);
        public abstract void onResponse(Call call, Response response, String responseStr) throws IOException;
    }

    public static abstract class Callback1 {
        public abstract void onFailure(Call call, IOException e);
        public abstract void onResponse(Call call, Response response) throws IOException;
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
