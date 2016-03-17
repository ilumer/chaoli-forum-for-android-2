package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by jianhao on 16-3-8.
 */
public class CookieUtils {
    /* 存储cookie并使用之前存储的cookie
    * context参数只是产生SharedPreference对象时使用，无论传递哪个context对象，都会返回一个拥有所有Cookie的CookieStore
    * 执行setCookieStore之后，CookieStore对象会保存Client对象产生的Cookie，
    * 同时Client对象也会使用CookieStore对象保存的Cookie来进行网络访问*/
    public static void saveCookie(AsyncHttpClient client, Context context) {
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        client.setCookieStore(cookieStore);
    }

    /* 得到cookie */
    public static List<Cookie> getCookie(Context context) {
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        List<Cookie> cookies = cookieStore.getCookies();
        return cookies;
    }

    /* 清除cookie */
    public static void clearCookie(Context context) {
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        cookieStore.clear();
    }






    /* 以下是废弃的部分，目测完全没有用，愚蠢的代码*/







    /*public static void clearLoginCookie(){
        loginCookieStore.clear();
    }*/

    /*public static void clearCookies(){
        cookies = null;
    }*/

    //private static List<Cookie> cookies;
    //private static PersistentCookieStore loginCookieStore;

    /* 存下登录页面对应的PersistentCookieStore，注销时使用 */
    /*public static void setLoginCookieStore(Context loginContext) {
        loginCookieStore = new PersistentCookieStore(loginContext);
    }*/

    /* 返回cookies列表 */
    /*public static List<Cookie> getCookies() {
        return cookies;//cookies != null ? cookies : new ArrayList<Cookie>();
    }*/

    /* 设置cookies列表 */
    /*public static void setCookies(List<Cookie> cookies) {
        CookieUtils.cookies = cookies;
    }*/
 }
