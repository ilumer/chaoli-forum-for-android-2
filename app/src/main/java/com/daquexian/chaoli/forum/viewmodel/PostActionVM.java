package com.daquexian.chaoli.forum.viewmodel;

import android.content.SharedPreferences;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import com.daquexian.chaoli.forum.ChaoliApplication;
import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.meta.Channel;
import com.daquexian.chaoli.forum.utils.ConversationUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jianhao on 16-9-21.
 */

public class PostActionVM extends BaseViewModel {
    private static final String TAG = "PostActionVM";
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();
    private String prevContent;
    public ObservableInt channelId = new ObservableInt();

    public ObservableBoolean postComplete = new ObservableBoolean(false);
    public ObservableInt showToast = new ObservableInt();
    public ObservableField<String> toastContent = new ObservableField<>();

    public ObservableBoolean updateRichText = new ObservableBoolean();
    public ObservableBoolean demoMode = new ObservableBoolean();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Channel preChannel = Channel.caff;
    private Channel curChannel;

    private static final String DRAFT_CONTENT = "draft_content";
    private static final String DRAFT_TITLE = "draft_title";
    private static final String DRAFT_CHANNEL = "draft_channel";

    public PostActionVM() {
        sharedPreferences = ChaoliApplication.getAppContext().getSharedPreferences(TAG, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        title.set(sharedPreferences.getString(DRAFT_TITLE, ""));
        content.set(sharedPreferences.getString(DRAFT_CONTENT, ""));
        prevContent = content.get();
        channelId.set(sharedPreferences.getInt(DRAFT_CHANNEL, Channel.caff.getChannelId()));
        curChannel = Channel.getChannel(channelId.get());

        content.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {

            }
        });
    }

    public void postConversation() {
        if (content.get().length() == 0) {
            toastContent.set(getString(R.string.content_cannot_be_null));
            showToast.notifyChange();
            return;
        }
        ConversationUtils.postConversation(title.get(), content.get(), new ConversationUtils.PostConversationObserver() {
            @Override
            public void onPostConversationSuccess(int conversationId) {
                Log.d(TAG, "onPostConversationSuccess() called with: conversationId = [" + conversationId + "]");
                editor.clear().apply();
                postComplete.notifyChange();
                //postComplete.set(true);
            }

            @Override
            public void onPostConversationFailure(int statusCode) {
                Log.d(TAG, "onPostConversationFailure() called with: statusCode = [" + statusCode + "]");
                toastContent.set(getString(R.string.network_err));
                showToast.notifyChange();
            }
        });
    }

    public void changeDemoMode() {
        demoMode.set(!demoMode.get());
    }

    public void doAfterContentChanged() {
        String newContent = content.get();
        if (newContent.equals(prevContent)) return;
        prevContent = newContent;
        updateRichText.notifyChange();
        saveContent(newContent);
    }

    public void setChannelId(final int channelId) {
        this.channelId.set(channelId);
        preChannel = curChannel;
        saveChannelId(channelId);
        ConversationUtils.setChannel(channelId, new ConversationUtils.SetChannelObserver() {
            @Override
            public void onSetChannelSuccess() {
                // what need to do has done in advance
            }

            @Override
            public void onSetChannelFailure(int statusCode) {   // restore
                showToast.notifyChange();
                toastContent.set(getString(R.string.network_err));

                PostActionVM.this.channelId.set(preChannel.getChannelId());
                curChannel = preChannel;
                saveChannelId(curChannel.getChannelId());
            }
        });
    }

    public void saveTitle(String title) {
        editor.putString(DRAFT_TITLE, title).apply();
    }

    private void saveContent(String content) {
        editor.putString(DRAFT_CONTENT, content).apply();
    }

    private void saveChannelId(int channelId) {
        editor.putInt(DRAFT_CHANNEL, channelId).apply();
    }
}
