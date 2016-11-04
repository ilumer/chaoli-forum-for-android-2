package com.daquexian.chaoli.forum.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.databinding.PostActionBinding;
import com.daquexian.chaoli.forum.meta.Channel;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;
import com.daquexian.chaoli.forum.viewmodel.PostActionVM;

/**
 * Created by jianhao on 16-5-31.
 */
public class PostAction extends BaseActivity implements IView {

    private static final String TAG = "PostAction";

    public static final int MENU_POST = 1;

    private Channel preChannel, curChannel;

    private PostActionVM viewModel;

    private PostActionBinding binding;

    private final Context mContext = this;

    //SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setViewModel(new PostActionVM());

        init();
    }

    private void init() {
        configToolbar(R.string.post);

        final String[] channelArr = {getString(R.string.channel_caff), getString(R.string.channel_maths), getString(R.string.channel_physics),
                getString(R.string.channel_biology),getString(R.string.channel_tech), getString(R.string.channel_lang),
                getString(R.string.channel_socsci)};

        binding.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.saveTitle(editable.toString());
            }
        });

        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.saveContent(editable.toString());
            }
        });

        binding.channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext).setTitle("选择板块").setItems(channelArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.setChannelId(Channel.getChannel(channelArr[which]).getChannelId());
                    }
                }).setCancelable(false).show();
            }
        });

        viewModel.postComplete.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                setResult(RESULT_OK);
                finish();
            }
        });

        viewModel.showToast.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                showToast(viewModel.toastContent.get());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, Menu.NONE, MENU_POST, R.string.post).setIcon(R.drawable.ic_cab_done_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch (item.getOrder())
        {
            case MENU_POST:
                Log.d(TAG, "onOptionsItemSelected: ");
                viewModel.postConversation();
                break;
        }
        return true;
    }

    @Override
    public void setViewModel(BaseViewModel viewModel) {
        this.viewModel = (PostActionVM) viewModel;
        binding = DataBindingUtil.setContentView(this, R.layout.post_action);
        binding.setViewModel(this.viewModel);
    }
}
