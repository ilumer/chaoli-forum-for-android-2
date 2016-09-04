package com.geno.chaoli.forum.app;

import android.app.Application;

/**
 * Created by jianhao on 16-8-25.
 */
public class ChaoliApplication extends Application {
    /*private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private ChaoliService service;*/
    @Override
    public void onCreate() {
        super.onCreate();
        /*okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ReceivedCookiesInterceptor(this))
                .addInterceptor(new AddCookiesInterceptor(this))
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(ChaoliService.class);*/
    }

    /*public OkHttpClient getClient(){
        return okHttpClient;
    }

    public ChaoliService getService(){
        return service;
    }*/
}
