package com.geno.chaoli.forum.view;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
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

import com.geno.chaoli.forum.data.Me;
import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.databinding.ActivitySettingsBinding;
import com.geno.chaoli.forum.utils.AccountUtils;
import com.geno.chaoli.forum.meta.AvatarView;

import java.io.File;

import com.bumptech.glide.*;
import com.geno.chaoli.forum.viewmodel.BaseViewModel;
import com.geno.chaoli.forum.viewmodel.SettingsVM;

/**
 * Created by jianhao on 16-3-12.
 */
public class SettingsActivity extends BaseActivity implements AccountUtils.GetProfileObserver{
    private static final String TAG = "SettingsActivity";

    SettingsVM viewModel;

    ProgressDialog progressDialog;

    Context mContext;
    AvatarView avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        viewModel.showProcessDialog.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (((ObservableBoolean) observable).get()) {
                    progressDialog = ProgressDialog.show(mContext, "", getString(R.string.just_a_sec));
                } else {
                    if (progressDialog != null) progressDialog.dismiss();
                }
            }
        });

        viewModel.showToast.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                showToast(viewModel.toastContent.get());
            }
        });

        viewModel.goToAlbum.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                goToAlbum();
            }
        });
    }

    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0;   //这里的IMAGE_CODE是自己任意定义的

//使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片

//重写onActivityResult以获得你需要的信息

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        /*if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
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
            Cursor cursor = resolver.query(originalUri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String selectedImagePath = cursor.getString(column_index);
            //Log.i("path", path);

            viewModel.avatarFile = new File(selectedImagePath);
            Glide.with(this).load(viewModel.avatarFile).into((ImageView)findViewById(R.id.iv_new_avatar));
        }*/
        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String selectedPath = getPath(selectedImage);
            Log.d(TAG, "onActivityResult: " + selectedPath);
            File selectedFile = new File(selectedPath);
            viewModel.avatarFile = selectedFile;
            ((AvatarView) findViewById(R.id.iv_avatar)).update(selectedFile);
        }
    }

    @Override
    public void onGetProfileSuccess() {
        updateViews();
    }

    @Override
    public void onGetProfileFailure() {

    }

    private void init() {
        setViewModel(new SettingsVM(Me.getMySignature(), Me.getMyStatus(), Me.getMyPrivateAdd(), Me.getMyStarOnReply(), Me.getMyStarPrivate(), Me.getMyHideOnline()));
        configToolbar(R.string.settings);

        mContext = this;
        avatar = (AvatarView)findViewById(R.id.iv_avatar);

        updateViews();
    }

    public void updateViews(){
        avatar.update(Me.getAvatarSuffix(), Me.getMyUserId(), Me.getUsername());
        //private_add_chk.setChecked(Me.getPreferences().getPrivateAdd());
        //star_on_reply_chk.setChecked(Me.getPreferences().getStarOnReply());
        //star_private_chk.setChecked(Me.getPreferences().getStarPrivate());
        //hide_online_chk.setChecked(Me.getPreferences().getHideOnline());
        //signature_edtTxt.setText(Me.getPreferences().getSignature());
        //user_status_edtTxt.setText(Me.getStatus());
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

    public void goToAlbum() {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(Intent.createChooser(getAlbum, "Select Picture"), IMAGE_CODE);
    }

    @Override
    public void setViewModel(BaseViewModel viewModel) {
        this.viewModel = (SettingsVM) viewModel;
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setViewModel(this.viewModel);
    }
}
