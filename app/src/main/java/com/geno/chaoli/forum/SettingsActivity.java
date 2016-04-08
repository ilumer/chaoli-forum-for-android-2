package com.geno.chaoli.forum;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.AccountUtils;
import com.geno.chaoli.forum.meta.ConversationUtils;
import com.geno.chaoli.forum.meta.LoginUtils;

import java.io.File;
import java.util.List;

import com.bumptech.glide.*;


/**
 * Created by jianhao on 16-3-12.
 */
public class SettingsActivity extends Activity implements View.OnClickListener, LoginUtils.LoginObserver,
        ConversationUtils.PostConversationObserver, ConversationUtils.SetChannelObserver,
        ConversationUtils.AddMemberObserver, ConversationUtils.GetMembersAllowedObserver,
        ConversationUtils.IgnoreAndStarConversationObserver,
        AccountUtils.GetProfileObserver{
    EditText username_txt, password_txt;
    TextView user_id_txt;

    Context mContext;
    File mAvatarFile;
    Toast mToast;
    ImageView avatar_iv;
    Button change_avatar_btn;
    Spinner language_spn;
    CheckBox private_add_chk;
    CheckBox star_on_reply_chk;
    CheckBox star_private_chk;
    CheckBox hide_online_chk;
    EditText signature_edtTxt;
    EditText user_status_edtTxt;
    Button save_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button change_avatar_btn = (Button)findViewById(R.id.btn_change_avatar);
        Spinner language_spn = (Spinner)findViewById(R.id.spn_language);
        private_add_chk = (CheckBox)findViewById(R.id.chk_private_add);
        star_on_reply_chk = (CheckBox)findViewById(R.id.chk_star_on_reply);
        star_private_chk = (CheckBox)findViewById(R.id.chk_star_private);
        hide_online_chk = (CheckBox)findViewById(R.id.chk_hide_online);
        signature_edtTxt = (EditText)findViewById(R.id.edtTxt_signature);
        user_status_edtTxt = (EditText)findViewById(R.id.edtTxt_user_status);
        save_btn = (Button)findViewById(R.id.btn_save);

        mContext = this;
        avatar_iv = (ImageView)findViewById(R.id.iv_avatar);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_change_avatar:
                        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                        getAlbum.setType(IMAGE_TYPE);
                        startActivityForResult(getAlbum, IMAGE_CODE);
                        break;
                    case R.id.btn_save:
                        mToast = Toast.makeText(mContext, "修改中，请稍候。。", Toast.LENGTH_LONG);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        mToast.show();
                        String signature = signature_edtTxt.getText().toString();
                        String user_status = user_status_edtTxt.getText().toString();
                        Boolean privateAdd = private_add_chk.isChecked();
                        Boolean starOnReply = star_on_reply_chk.isChecked();
                        Boolean starPrivate = star_private_chk.isChecked();
                        Boolean hideOnline = hide_online_chk.isChecked();
                        AccountUtils.modifySettings(mContext, mAvatarFile, "Chinese", privateAdd, starOnReply,
                                starPrivate, hideOnline, signature, user_status, new AccountUtils.ModifySettingsObserver() {
                                    @Override
                                    public void onModifySettingsSuccess() {
                                        if(mToast != null) mToast.cancel();
                                        mToast = Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }

                                    @Override
                                    public void onModifySettingsFailure(int statusCode) {
                                        if(mToast != null) mToast.cancel();
                                        mToast = Toast.makeText(mContext, "修改失败，请稍后重试", Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                });
                        break;
                    default:
                        break;
                }
            }
        };
        change_avatar_btn.setOnClickListener(onClickListener);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_dropdown_item);
        language_spn.setAdapter(adapter);
        save_btn.setOnClickListener(onClickListener);

        LoginUtils.begin_login(this, this);
    }

    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0;   //这里的IMAGE_CODE是自己任意定义的

//使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片

//重写onActivityResult以获得你需要的信息

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            Log.e("error","ActivityResult resultCode error");
            return;
        }

        //Bitmap bm = null;

        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();

        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode == IMAGE_CODE) {
            Uri originalUri = data.getData();        //获得图片的uri
            Log.i("uri", originalUri.toString());
            //bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片

            String[] proj = {MediaStore.Images.Media.DATA};

            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(column_index);
            Log.i("path", path);
            mAvatarFile = new File(path);
            Glide.with(this).load(mAvatarFile).into((ImageView)findViewById(R.id.iv_new_avatar));
            Log.i("name", mAvatarFile.getName());
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLoginSuccess(int userId, String token) {
        //user_id_txt.setText(String.valueOf(userId));
        Log.i("login", String.valueOf(userId));

        AccountUtils.getProfile(this, this);
    }

    @Override
    public void onLoginFailure(int statusCode) {
        Log.e("login error", String.valueOf(statusCode));
        if(statusCode == LoginUtils.COOKIE_EXPIRED){
            LoginUtils.begin_login(this, this);
        }
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

    @Override
    public void onGetProfileSuccess() {
        Glide.with(mContext).load("https://dn-chaoli-upload.qbox.me/avatar_" + LoginUtils.getUserId() + "." + Me.getMyAvatarSuffix()).into(avatar_iv);
                private_add_chk.setChecked(Me.getMyPrivateAdd());
        star_on_reply_chk.setChecked(Me.getMyStarOnReply());
        star_private_chk.setChecked(Me.getMyStarPrivate());
        hide_online_chk.setChecked(Me.getMyHideOnline());
        signature_edtTxt.setText(Me.getMySignature());
        user_status_edtTxt.setText(Me.getMyStatus());
    }

    @Override
    public void onGetProfileFailure() {

    }
}
