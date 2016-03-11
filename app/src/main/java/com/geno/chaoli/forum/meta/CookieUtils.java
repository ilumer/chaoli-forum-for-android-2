package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by jianhao on 16-3-8.
 */
public class CookieUtils {
    private static List<Cookie> cookies;
    private static PersistentCookieStore loginCookieStore;

    /* 存下登录页面对应的PersistentCookieStore，注销时使用 */
    public static void setLoginCookieStore(Context loginContext) {
        loginCookieStore = new PersistentCookieStore(loginContext);
    }

    /* 返回cookies列表 */
    public static List<Cookie> getCookies() {
        return cookies;//cookies != null ? cookies : new ArrayList<Cookie>();
    }

    /* 设置cookies列表 */
    public static void setCookies(List<Cookie> cookies) {
        CookieUtils.cookies = cookies;
    }

    /* 存储cookie */
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

    public static void clearLoginCookie(){
        loginCookieStore.clear();
    }

    public static void clearCookies(){
        cookies = null;
    }
 }
