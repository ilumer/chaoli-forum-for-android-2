package com.daquexian.chaoli.forum.view;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
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
import com.daquexian.chaoli.forum.meta.SFXParser3;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;
import com.daquexian.chaoli.forum.viewmodel.PostActionVM;

/**
 * Created by jianhao on 16-5-31.
 */
public class PostAction extends BaseActivity implements IView {

    private static final String TAG = "PostAction";

    public static final int MENU_POST = 2;
    public static final int MENU_DEMO = 1;

    private PostActionVM viewModel;
    private PostActionBinding binding;

    /* 切换至演示模式时保存光标位置，切换回普通模式时恢复 */
    private int selectionStart, selectionEnd;

    private final Context mContext = this;

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

        viewModel.updateContentRichText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                int selectionStart = binding.content.getSelectionStart();
                int selectionEnd = binding.content.getSelectionEnd();
                binding.content.setText(SFXParser3.parse(getApplicationContext(), viewModel.content.get(), null));
                binding.content.setSelection(selectionStart, selectionEnd);
            }
        });

        viewModel.updateTitleRichText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                int selectionStart = binding.title.getSelectionStart();
                int selectionEnd = binding.title.getSelectionEnd();
                binding.title.setText(SFXParser3.parse(getApplicationContext(), viewModel.title.get(), null));
                binding.title.setSelection(selectionStart, selectionEnd);
            }
        });

        viewModel.demoMode.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (((ObservableBoolean) observable).get()) {
                    selectionStart = binding.content.getSelectionStart();
                    selectionEnd = binding.content.getSelectionEnd();
                    binding.content.setEnabled(false);
                    binding.content.setOnlineImgEnabled(true);
                    binding.content.update();
                } else {
                    binding.content.setEnabled(true);
                    binding.content.setOnlineImgEnabled(false);
                    binding.content.update();
                    binding.content.setSelection(selectionStart, selectionEnd);
                }
            }
        });

        binding.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.doAfterTitleChanged();
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
                viewModel.doAfterContentChanged();
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

        viewModel.showWelcome.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                new AlertDialog.Builder(mContext).setMessage(R.string.welcome_to_demo_mode)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, Menu.NONE, MENU_DEMO, R.string.post).setIcon(R.drawable.ic_functions_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
            case MENU_DEMO:
                viewModel.changeDemoMode();
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
