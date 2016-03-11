package com.geno.chaoli.forum.meta;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
/**
 * 请在LoginObverser的onLoginFailure的实现中执行LoginUtils.clear()方法
 * 可通过CookieUtils.getCookies()直接获取到登录后的cookie
 */
public class LoginUtils {
    public static final String LOGIN_URL = "https://chaoli.club/index.php/user/login?return=%2F";
    public static final String HOMEPAGE_URL = "https://chaoli.club/index.php";
    public static final String LOGOUT_PRE_URL = "https://chaoli.club/index.php/user/logout?token=";
    public static final String COOKIE_UN_AND_PW = "im^#@cookie^$&";
    public static final String LOGIN_SP_NAME = "username_and_password";
    public static final String SP_USERNAME_KEY = "username";
    public static final String SP_PASSWORD_KEY = "password";
    public static final int FAILED_AT_OPEN_LOGIN_PAGE = 0;
    public static final int FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE = 1;
    public static final int FAILED_AT_LOGIN = 2;
    public static final int WRONG_USERNAME_OR_PASSWORD = 3;
    public static final int FAILED_AT_OPEN_HOMEPAGE = 4;
    public static final int COOKIE_EXPIRED = 5;
    public static final int EMPTY_UN_OR_PW = 6;

    private static void setToken(String token) {
        LoginUtils.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static int getUserId() {
        return userId;
    }

    private static void setUserId(int userId) {
        LoginUtils.userId = userId;
    }

    private static String username;
    private static String password;
    private static String token;
    private static int userId;
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static SharedPreferences sharedPreferences;

    public static void begin_login(final Context context, String username, String password, LoginObverser loginObverser){
        //Log.i("login_1", "username = " + username + ", password = " + password);
        if(CookieUtils.getCookie(context).size() == 0){
            LoginUtils.username = username;
            LoginUtils.password = password;
            //Log.i("cookie", "doesn't exists");
            pre_login(context, loginObverser);
        }
    }

    public static void begin_login(final Context context, LoginObverser loginObverser){
        CookieUtils.setLoginCookieStore(context);
        CookieUtils.saveCookie(client, context);

        if(CookieUtils.getCookie(context).size() != 0){
            //Log.i("cookie", "exists");
            getNewToken(context, loginObverser);
            username = password = COOKIE_UN_AND_PW;
            return;
        }

        //Log.i("login_2", "username = " + username + ", password = " + password);
        sharedPreferences = context.getSharedPreferences(LOGIN_SP_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(SP_USERNAME_KEY, "");
        password = sharedPreferences.getString(SP_PASSWORD_KEY, "");

        if("".equals(username) || "".equals(password)){
            loginObverser.onLoginFailure(EMPTY_UN_OR_PW);
            return;
        }

        begin_login(context, username, password, loginObverser);
    }

    private static void pre_login(final Context context, final LoginObverser loginObverser){//获取登录页面的token
        client.get(context, LOGIN_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                String tokenFormat = "\"token\":\"([\\dabcdef]+)";
                Pattern pattern = Pattern.compile(tokenFormat);
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {
                    //Log.i("login_token", matcher.group(1));
                    setToken(matcher.group(1));
                    login(context, loginObverser);
                } else {
                    //Log.e("regex_error", "regex_error");
                    loginObverser.onLoginFailure(FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE);
                }
                //Log.i("login_page", response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Log.e("login_error", "");
                loginObverser.onLoginFailure(FAILED_AT_OPEN_LOGIN_PAGE);
            }
        });
    }

    private static void login(final Context context, final LoginObverser loginObverser){ //发送请求登录
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("return", "/");
        params.put("login", "登录");
        params.put("token", getToken());
        client.post(context, LOGIN_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Log.i("after_login", new String(responseBody));
                loginObverser.onLoginFailure(WRONG_USERNAME_OR_PASSWORD);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                error.printStackTrace(pw);
                //Log.i("error", sw.toString());
                if ("Moved Temporarily".equals(error.getMessage())) { //表示登陆成功，若在浏览器中将会跳转到首页
                    getNewToken(context, loginObverser);
                } else {
                    loginObverser.onLoginFailure(FAILED_AT_LOGIN);
                }
            }
        });
    }

    private static void getNewToken(final Context context, final LoginObverser loginObverser){ //得到新的token
        CookieUtils.saveCookie(client, context);
        client.get(context, HOMEPAGE_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                //Log.i("homepage", response);
                String tokenFormat = "\"userId\":(\\d+),\"token\":\"([\\dabcdef]+)";
                Pattern pattern = Pattern.compile(tokenFormat);
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {
                    setUserId(Integer.parseInt(matcher.group(1)));
                    setToken(matcher.group(2));
                    //Log.i("newToken", getToken());
                    //Log.i("userId", String.valueOf(getUserId()));
                    sharedPreferences = context.getSharedPreferences(LOGIN_SP_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //不是用cookie登录
                    if (!COOKIE_UN_AND_PW.equals(username)) {
                        editor.putString(SP_USERNAME_KEY, username);
                        editor.putString(SP_PASSWORD_KEY, password);
                        editor.apply();
                    }
                    CookieUtils.setCookies(CookieUtils.getCookie(context));
                    loginObverser.onLoginSuccess(getUserId(), getToken());
                } else {
                    loginObverser.onLoginFailure(COOKIE_EXPIRED);
                    //Log.e("regex_error", "regex_error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loginObverser.onLoginFailure(FAILED_AT_OPEN_HOMEPAGE);
            }
        });
    }

    public static void logout(final Context context, final LogoutObverser logoutObverser){
        String logoutURL = LOGOUT_PRE_URL + getToken();
        client.get(context, logoutURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                clear();
                //Log.i("logout", new String(responseBody));
                //Log.i("cookie", String.valueOf(CookieUtils.getCookie(context).size()));
                logoutObverser.onLogoutSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                logoutObverser.onLogoutFailure(statusCode);
            }
        });
    }

    public static void clear(){
        CookieUtils.clearLoginCookie();
        CookieUtils.clearCookies();
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SP_USERNAME_KEY);
            editor.remove(SP_PASSWORD_KEY);
            editor.apply();
        }
    }

    public interface LoginObverser{
        public void onLoginSuccess(int userId, String token);
        public void onLoginFailure(int statusCode);
    }

    public interface LogoutObverser{
        public void onLogoutSuccess();
        public void onLogoutFailure(int statusCode);
    }
}
