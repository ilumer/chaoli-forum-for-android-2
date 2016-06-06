package com.geno.chaoli.forum;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.geno.chaoli.forum.meta.Constants;

/**
 * Created by daquexian on 16-4-8.
 * 保存用户自己的账户信息的类
 */
public class Me implements Parcelable {
    private static final String TAG = "Me";
    private boolean isEmpty = true;
    private int userId;
    private String username;

    @JSONField(name="avatarFormat")
    private String avatarSuffix;
    private String status;
    private Preferences preferences;
    private static Me me;

    private Me(){}

    private Me(Parcel in){
        userId = in.readInt();
        username = in.readString();
        avatarSuffix = in.readString();
        status = in.readString();
        preferences.privateAdd = (in.readByte() == 1);
        preferences.starOnReply = (in.readByte() == 1);
        preferences.starPrivate = (in.readByte() == 1);
        preferences.hideOnline = (in.readByte() == 1);
    }

    private static Me getInstance(){
        if(me == null)
            me = new Me();
        return me;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(username);
        dest.writeString(avatarSuffix);
        dest.writeString(status);
        dest.writeString(preferences.signature);
        dest.writeByte((byte)(preferences.privateAdd ? 1 : 0));
        dest.writeByte((byte)(preferences.starOnReply ? 1 : 0));
        dest.writeByte((byte)(preferences.starPrivate ? 1 : 0));
        dest.writeByte((byte)(preferences.hideOnline ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static final Parcelable.Creator<Me> CREATOR
            = new Parcelable.Creator<Me>() {
        public Me createFromParcel(Parcel in) {
            return new Me(in);
        }

        public Me[] newArray(int size) {
            return new Me[size];
        }
    };


    public static void clear(){
        me = new Me();
    }

    public static boolean isEmpty(){
        return Me.getInstance().isEmpty;
    }

    public static int getMyUserId(){
        return Me.getInstance().userId;
    }

    public static String getMyUsername(){
        return Me.getInstance().username;
    }

    public static String getMyAvatarSuffix(){
        return Me.getInstance().avatarSuffix;
    }

    public static String getMyAvatarURL(){
        return Constants.avatarURL + "avatar_" + getMyUserId() + "." + getMyAvatarSuffix();
    }

    public static String getMyStatus(){
        return Me.getInstance().status;
    }

    public static String getMySignature(){
        return Me.getInstance().preferences.signature != null ? Me.getInstance().preferences.signature : "";
    }

    public static Boolean getMyPrivateAdd(){
        return Me.getInstance().preferences.privateAdd;
    }

    public static Boolean getMyStarOnReply(){
        return Me.getInstance().preferences.starOnReply;
    }

    public static Boolean getMyStarPrivate(){
        return Me.getInstance().preferences.starPrivate;
    }

    public static Boolean getMyHideOnline(){
        return Me.getInstance().preferences.hideOnline != null ? Me.getInstance().preferences.hideOnline : false;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public static void setUsername(String username){
        Me.getInstance().username = username;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }


    public static int getUserId() {
        return getMyUserId();
    }

    public static void setUserId(int userId) {
        Log.d("me", "id = " + Me.getInstance().userId);
        Me.getInstance().userId = userId;
        Log.d("me", "id = " + Me.getInstance().userId);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarSuffix() {
        return avatarSuffix;
    }

    public void setAvatarSuffix(String avatarSuffix) {
        this.avatarSuffix = avatarSuffix;
    }

    public static class Preferences{
        private String signature;
        @JSONField(name="email.privateAdd")
        private Boolean privateAdd;
        private Boolean starOnReply;
        private Boolean starPrivate;
        private Boolean hideOnline;

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public Boolean getPrivateAdd() {
            return privateAdd;
        }

        public void setPrivateAdd(Boolean privateAdd) {
            this.privateAdd = privateAdd;
        }

        public Boolean getStarOnReply() {
            return starOnReply;
        }

        public void setStarOnReply(Boolean starOnReply) {
            this.starOnReply = starOnReply;
        }

        public Boolean getStarPrivate() {
            return starPrivate;
        }

        public void setStarPrivate(Boolean starPrivate) {
            this.starPrivate = starPrivate;
        }

        public Boolean getHideOnline() {
            return hideOnline;
        }

        public void setHideOnline(Boolean hideOnline) {
            this.hideOnline = hideOnline;
        }
    }

    public static void setInstanceFromJSONStr(Context context, String jsonStr){
        Me me2 = JSON.parseObject(jsonStr, Me.class);
        me2.userId = me.userId;
        me2.username = me.username;
        me = me2;
        me.isEmpty = false;
        if(getMyAvatarSuffix() == null){
            me.setAvatarSuffix(Constants.NONE);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getMyUsername(), JSON.toJSONString(me));
        editor.apply();
    }
    public static void setInstanceFromSharedPreference(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(sharedPreferences.contains(username)) {
            String info = sharedPreferences.getString(username, "bing mei you");
            setInstanceFromJSONStr(context, info);
        }
    }
}
