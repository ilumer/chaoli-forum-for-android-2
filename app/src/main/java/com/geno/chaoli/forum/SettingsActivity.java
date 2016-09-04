package com.geno.chaoli.forum;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.utils.AccountUtils;
import com.geno.chaoli.forum.meta.AvatarView;

import java.io.File;

import com.bumptech.glide.*;


/**
 * Created by jianhao on 16-3-12.
 */
public class SettingsActivity extends BaseActivity implements AccountUtils.GetProfileObserver{
    private static final String TAG = "SettingsActivity";
    EditText username_txt, password_txt;
    TextView user_id_txt;

    Context mContext;
    File mAvatarFile;
    Toast mToast;
    AvatarView avatar;
    Button change_avatar_btn;
    //Spinner language_spn;
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
        configToolbar(R.string.settings);

        Button change_avatar_btn = (Button)findViewById(R.id.btn_change_avatar);
        //Spinner language_spn = (Spinner)findViewById(R.id.spn_language);
        private_add_chk = (CheckBox)findViewById(R.id.chk_private_add);
        star_on_reply_chk = (CheckBox)findViewById(R.id.chk_star_on_reply);
        star_private_chk = (CheckBox)findViewById(R.id.chk_star_private);
        hide_online_chk = (CheckBox)findViewById(R.id.chk_hide_online);
        signature_edtTxt = (EditText)findViewById(R.id.edtTxt_signature);
        user_status_edtTxt = (EditText)findViewById(R.id.edtTxt_user_status);
        save_btn = (Button)findViewById(R.id.btn_save);

        mContext = this;
        avatar = (AvatarView)findViewById(R.id.iv_avatar);

        updateViews();

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
                        String signature = signature_edtTxt.getText().toString();
                        String user_status = user_status_edtTxt.getText().toString();
                        Boolean privateAdd = private_add_chk.isChecked();
                        Boolean starOnReply = star_on_reply_chk.isChecked();
                        Boolean starPrivate = star_private_chk.isChecked();
                        Boolean hideOnline = hide_online_chk.isChecked();
                        final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", getResources().getString(R.string.just_a_sec));
                        progressDialog.show();
                        AccountUtils.modifySettings(mContext, mAvatarFile, "Chinese", privateAdd, starOnReply,
                                starPrivate, hideOnline, signature, user_status, new AccountUtils.ModifySettingsObserver() {
                                    @Override
                                    public void onModifySettingsSuccess() {
                                        AccountUtils.getProfile(mContext, new AccountUtils.GetProfileObserver() {
                                            @Override
                                            public void onGetProfileSuccess() {
                                                progressDialog.dismiss();
                                                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onGetProfileFailure() {
                                                progressDialog.dismiss();
                                                Toast.makeText(mContext, R.string.network_err, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onModifySettingsFailure(int statusCode) {
                                        progressDialog.dismiss();
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
        //language_spn.setAdapter(adapter);
        save_btn.setOnClickListener(onClickListener);
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
            /*Uri originalUri = data.getData();        //获得图片的uri
            Log.i("uri", originalUri.toString());
            //bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片

            String[] proj = {MediaStore.Images.Media.DATA};

            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = resolver.query(originalUri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String selectedImagePath = cursor.getString(column_index);
            //Log.i("path", path);
*/
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: " + selectedImageUri.toString());
            String selectedImagePath = getPath(selectedImageUri);
            Log.d(TAG, "onActivityResult: " + selectedImagePath);
            mAvatarFile = new File(selectedImagePath);
            Glide.with(this).load(mAvatarFile).into((ImageView)findViewById(R.id.iv_new_avatar));
            Log.i("name", mAvatarFile.getName());
        }
    }

    @Override
    public void onGetProfileSuccess() {
        updateViews();
    }

    @Override
    public void onGetProfileFailure() {

    }

    public void updateViews(){
        avatar.update(this, Me.getAvatarSuffix(), Me.getMyUserId(), Me.getUsername());
        private_add_chk.setChecked(Me.getPreferences().getPrivateAdd());
        star_on_reply_chk.setChecked(Me.getPreferences().getStarOnReply());
        star_private_chk.setChecked(Me.getPreferences().getStarPrivate());
        hide_online_chk.setChecked(Me.getPreferences().getHideOnline());
        signature_edtTxt.setText(Me.getPreferences().getSignature());
        user_status_edtTxt.setText(Me.getStatus());
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
