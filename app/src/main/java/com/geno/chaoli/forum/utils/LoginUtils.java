package com.geno.chaoli.forum.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.geno.chaoli.forum.Me;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.network.MyOkHttp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginUtils {
    private static final String TAG = "LoginUtils";
    public static final String LOGIN_SP_NAME = "username_and_password";
    public static final String IS_LOGGED_IN = "is_logged_in";
    public static final String SP_USERNAME_KEY = "username";
    public static final String SP_PASSWORD_KEY = "password";
    public static final int FAILED_AT_OPEN_LOGIN_PAGE = 0;
    public static final int FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE = 1;
    public static final int FAILED_AT_LOGIN = 2;
    public static final int WRONG_USERNAME_OR_PASSWORD = 3;
    public static final int FAILED_AT_OPEN_HOMEPAGE = 4;
    public static final int COOKIE_EXPIRED = 5;
    public static final int EMPTY_UN_OR_PW = 6;
    public static final int ERROR_LOGIN_STATUS = 7;

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

    private static SharedPreferences sharedPreferences;

    public static void begin_login(final Context context, final String username, final String password, final LoginObserver loginObserver){
        sharedPreferences = context.getSharedPreferences(LOGIN_SP_NAME, Context.MODE_PRIVATE);

        if( !sharedPreferences.getBoolean(IS_LOGGED_IN, false)){
            LoginUtils.username = username;
            LoginUtils.password = password;
            pre_login(context, loginObserver);
        }else{
            //如果已经登录，先注销
            logout(context, new LogoutObserver() {
                @Override
                public void onLogoutSuccess() {
                    LoginUtils.username = username;
                    LoginUtils.password = password;
                    pre_login(context, loginObserver);
                }

                @Override
                public void onLogoutFailure(int statusCode) {
                    loginObserver.onLoginFailure(ERROR_LOGIN_STATUS);
                }
            });
        }
    }

    public static void begin_login(final Context context, LoginObserver loginObserver){

        sharedPreferences = context.getSharedPreferences(LOGIN_SP_NAME, Context.MODE_PRIVATE);
        Boolean is_logged_in = sharedPreferences.getBoolean(IS_LOGGED_IN, false);

        //if(CookieUtils.getCookie(context).size() != 0){

        username = sharedPreferences.getString(SP_USERNAME_KEY, "");
        password = sharedPreferences.getString(SP_PASSWORD_KEY, "");

        if(is_logged_in){
            getNewToken(context, loginObserver);
            //username = password = COOKIE_UN_AND_PW;
            return;
        }

        if("".equals(username) || "".equals(password)){
            loginObserver.onLoginFailure(EMPTY_UN_OR_PW);
            return;
        }

        Log.d("login", username + ", " + password);

        begin_login(context, username, password, loginObserver);
    }

    /*public static void myLogin(final Context context, final String username, final String password, final LoginObserver loginObserver) {
        Log.d(TAG, "myLogin() called with: " + "context = [" + context + "], username = [" + username + "], password = [" + password + "], loginObserver = [" + loginObserver + "]");
        MyRetrofit.getService().getToken()
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String responseStr) {
                            String tokenFormat = "\"token\":\"([\\dabcdef]+)";
                            Pattern pattern = Pattern.compile(tokenFormat);
                            Matcher matcher = pattern.matcher(responseStr);
                            if (matcher.find()) {
                                String token = matcher.group(1);
                                setToken(token);
                                //login(context, loginObserver);
                                return MyRetrofit.getService().login(username, password, token);
                            } else {
                                //Log.e("regex_error", "regex_error");
                                CookieUtils.clearCookie(context);
                                loginObserver.onLoginFailure(FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE);
                                return null;
                            }
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String responseStr) {
                            Log.d(TAG, "onResponse: " + responseStr);
                            String tokenFormat = "\"userId\":(\\d+),\"token\":\"([\\dabcdef]+)";
                            Pattern pattern = Pattern.compile(tokenFormat);
                            Matcher matcher = pattern.matcher(responseStr);
                            if (matcher.find()) {
                                int userId = Integer.parseInt(matcher.group(1));
                                setUserId(userId);
                                User.setUserId(userId);

                                setToken(matcher.group(2));

                                saveUsernameAndPassword(context, username, password);
                                //CookieUtils.setCookies(CookieUtils.getCookie(context));
                                setSPIsLoggedIn(true);
                                User.setUsername(username);
                                loginObserver.onLoginSuccess(getUserId(), getToken());
                            } else {
                                CookieUtils.clearCookie(context);
                                setSPIsLoggedIn(false);
                                //loginObserver.onLoginFailure(COOKIE_EXPIRED);
                                //begin_login(context, loginObserver);
                                //Log.e("regex_error", "regex_error");
                            }
                    }
                });
    }*/

    private static void pre_login(final Context context, final LoginObserver loginObserver){//获取登录页面的token
        new MyOkHttp.MyOkHttpClient()
                .get(Constants.LOGIN_URL)
                .enqueue(context, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        loginObserver.onLoginFailure(FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.body().string();
                        String tokenFormat = "\"token\":\"([\\dabcdef]+)";
                        Pattern pattern = Pattern.compile(tokenFormat);
                        Matcher matcher = pattern.matcher(responseStr);
                        if (matcher.find()) {
                            setToken(matcher.group(1));
                            login(context, loginObserver);
                        } else {
                            //Log.e("regex_error", "regex_error");
                            loginObserver.onLoginFailure(FAILED_AT_GET_TOKEN_ON_LOGIN_PAGE);
                        }
                    }
                });
    }

    private static void login(final Context context, final LoginObserver loginObserver){ //发送请求登录
        new MyOkHttp.MyOkHttpClient()
                .add("username", username)
                .add("password", password)
                .add("return", "/")
                .add("login", "登录")
                .add("token", getToken())
                .post(Constants.LOGIN_URL)
                .enqueue(context, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        setSPIsLoggedIn(false);
                        loginObserver.onLoginFailure(COOKIE_EXPIRED);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.body().string();
                        String tokenFormat = "\"userId\":(\\d+),\"token\":\"([\\dabcdef]+)";
                        Pattern pattern = Pattern.compile(tokenFormat);
                        Matcher matcher = pattern.matcher(responseStr);
                        if (matcher.find()) {
                            int userId = Integer.parseInt(matcher.group(1));
                            setUserId(userId);
                            Me.setUserId(userId);

                            setToken(matcher.group(2));

                            saveUsernameAndPassword(context, username, password);
                            //CookieUtils.setCookies(CookieUtils.getCookie(context));
                            setSPIsLoggedIn(true);
                            Me.setUsername(username);
                            loginObserver.onLoginSuccess(getUserId(), getToken());
                        } else {
                            setSPIsLoggedIn(false);
                            loginObserver.onLoginFailure(COOKIE_EXPIRED);
                            //begin_login(context, loginObserver);
                            //Log.e("regex_error", "regex_error");
                        }

                    }
                });
    }

    private static void getNewToken(final Context context, final LoginObserver loginObserver){ //得到新的token
        new MyOkHttp.MyOkHttpClient().get(Constants.HOMEPAGE_URL)
                .enqueue(context, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        setSPIsLoggedIn(false);
                        begin_login(context, loginObserver);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.body().string();
                        Log.d(TAG, "onResponse: " + responseStr);
                        String tokenFormat = "\"userId\":(\\d+),\"token\":\"([\\dabcdef]+)";
                        Pattern pattern = Pattern.compile(tokenFormat);
                        Matcher matcher = pattern.matcher(responseStr);
                        if (matcher.find()) {
                            Log.d(TAG, "onResponse: found");
                            int userId = Integer.parseInt(matcher.group(1));
                            setUserId(userId);
                            Me.setUserId(userId);

                            setToken(matcher.group(2));

                            saveUsernameAndPassword(context, username, password);
                            //CookieUtils.setCookies(CookieUtils.getCookie(context));
                            setSPIsLoggedIn(true);
                            Me.setUsername(username);
                            loginObserver.onLoginSuccess(getUserId(), getToken());
                        } else {
                            Log.d(TAG, "onResponse: not found");
                            setSPIsLoggedIn(false);
                            //loginObserver.onLoginFailure(COOKIE_EXPIRED);
                            begin_login(context, loginObserver);
                            //Log.e("regex_error", "regex_error");
                        }
                    }
                });
    }

    public static void logout(final Context context, final LogoutObserver logoutObserver){
        String logoutURL = Constants.LOGOUT_PRE_URL + getToken();
        clear(context);
        Me.clear();
        new MyOkHttp.MyOkHttpClient()
                .get(logoutURL)
                .enqueue(context, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        logoutObserver.onLogoutSuccess();
                    }
                });
        /*client.get(context, logoutURL, new AsyncHttpResponseHandler() { //与服务器通信的作用似乎只是告诉服务器我下线了而已
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                logoutObserver.onLogoutSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, String.valueOf(statusCode));
                logoutObserver.onLogoutFailure(statusCode);
            }
        });*/
    }

    public static void clear(Context context){
        MyOkHttp.clearCookie();
        sharedPreferences = context.getSharedPreferences(LOGIN_SP_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(IS_LOGGED_IN);
            editor.remove(SP_USERNAME_KEY);
            editor.remove(SP_PASSWORD_KEY);
            editor.apply();
        }
    }

    private static void setSPIsLoggedIn(Boolean isLoggedIn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public static Boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public static void saveUsernameAndPassword(Context context, String username, String password){
        sharedPreferences = context.getSharedPreferences(LOGIN_SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SP_USERNAME_KEY, username);
        // TODO: 16-3-11 1915 Encrypt saved password
        editor.putString(SP_PASSWORD_KEY, password);
        editor.apply();
    }

    public interface LoginObserver
    {
        void onLoginSuccess(int userId, String token);
        void onLoginFailure(int statusCode);
    }

    public interface LogoutObserver
    {
        void onLogoutSuccess();
        void onLogoutFailure(int statusCode);
    }
}
