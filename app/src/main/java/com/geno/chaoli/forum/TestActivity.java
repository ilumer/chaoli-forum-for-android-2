package com.geno.chaoli.forum;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.ConversationUtils;
import com.geno.chaoli.forum.meta.CookieUtils;
import com.geno.chaoli.forum.meta.LoginUtils;

import java.util.List;

/**
 * Created by jianhao on 16-3-12.
 */
public class TestActivity extends Activity implements View.OnClickListener, LoginUtils.LoginObserver,
        ConversationUtils.PostConversationObserver, ConversationUtils.SetChannelObserver,
        ConversationUtils.AddMemberObserver, ConversationUtils.GetMembersAllowedObserver,
        ConversationUtils.IgnoreAndStarConversationObserver{
    EditText username_txt, password_txt;
    TextView user_id_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button login_btn = (Button) findViewById(R.id.login_btn);
        Button set_channel_btn = (Button) findViewById(R.id.set_channel_btn);
        username_txt = (EditText) findViewById(R.id.username_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        user_id_txt = (TextView) findViewById(R.id.user_id_txt);

        login_btn.setOnClickListener(this);
        set_channel_btn.setOnClickListener(this);

        LoginUtils.begin_login(this, this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                LoginUtils.begin_login(this, username_txt.getText().toString(),
                        password_txt.getText().toString(), this);
                break;
            case R.id.set_channel_btn:
                ConversationUtils.starConversation(this, 2237, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoginSuccess(int userId, String token) {
        user_id_txt.setText(String.valueOf(userId));
        Log.i("login", "success");
    }

    @Override
    public void onLoginFailure(int statusCode) {
        Log.e("login error", String.valueOf(statusCode));
    }

    @Override
    public void onPostConversationSuccess(int conversationId) {
        Log.i("post", String.valueOf(conversationId));
    }

    @Override
    public void onPostConversationFailure(int statusCode) {
        Log.i("post", "success");
    }

    @Override
    public void onSetChannelSuccess() {
        ConversationUtils.addMember(this, "我是大缺弦", this);
    }

    @Override
    public void onSetChannelFailure(int statusCode) {

    }

    @Override
    public void onAddMemberFailure(int statusCode) {

    }

    @Override
    public void onAddMemberSuccess() {
        ConversationUtils.postConversation(this, "发帖测试", "发帖测试", this);
    }

    @Override
    public void onGetMembersAllowedFailure(int statusCode) {
        Log.e("member", String.valueOf(statusCode));
    }

    @Override
    public void onGetMembersAllowedSuccess(List<Integer> memberList) {
        Log.i("size", String.valueOf(memberList.size()));
        Log.i("member1", String.valueOf(memberList.get(0)));
        for (Integer i:
             memberList) {
            Log.i("member", i.toString());
        }
    }

    @Override
    public void onIgnoreConversationFailure(int statusCode) {
        Log.e("ignore", String.valueOf(statusCode));
    }

    @Override
    public void onIgnoreConversationSuccess(Boolean isIgnored) {
        if(isIgnored){
            Toast.makeText(this, "已被隐藏", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "已取消隐藏", Toast.LENGTH_SHORT).show();
        }
        Log.i("ignore", String.valueOf(isIgnored));
    }

    @Override
    public void onStarConversationFailure(int statusCode) {
        Log.e("star", String.valueOf(statusCode));
    }

    @Override
    public void onStarConversationSuccess(Boolean isStarred) {
        if(isStarred){
            Toast.makeText(this, "已关注", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "已取消关注", Toast.LENGTH_SHORT).show();
        }
        Log.i("ignore", String.valueOf(isStarred));
    }
}
