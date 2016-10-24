package com.geno.chaoli.forum.viewmodel;

import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.geno.chaoli.forum.R;
import com.geno.chaoli.forum.utils.PostUtils;
import com.geno.chaoli.forum.view.IView;
import com.geno.chaoli.forum.view.ReplyAction;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jianhao on 16-9-21.
 */

public class ReplyActionVM extends BaseViewModel {
    private static final String TAG = "ReplyActionVM";

    public ObservableInt flag = new ObservableInt();
    public ObservableInt conversationId = new ObservableInt();
    public ObservableInt postId = new ObservableInt();
    public ObservableField<String> replyTo = new ObservableField<>();
    public ObservableField<String> replyMsg = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>("");
    public ObservableInt selection = new ObservableInt();

    public ObservableInt showToast = new ObservableInt();
    public ObservableField<String> toastContent = new ObservableField<>();
    public ObservableBoolean replyComplete = new ObservableBoolean(false);
    public ObservableBoolean editComplete = new ObservableBoolean(false);

    private SharedPreferences sp;
    private SharedPreferences.Editor e;

    public ReplyActionVM(int flag, int conversationId, int postId, String replyTo, String replyMsg) {
        this.flag.set(flag);
        this.conversationId.set(conversationId);
        this.postId.set(postId);
        this.replyTo.set(replyTo);
        this.replyMsg.set(replyMsg);
        sp = getSharedPreferences(TAG, MODE_PRIVATE);
        e = sp.edit();
        String draft = sp.getString(String.valueOf(conversationId), "");
        if (!"".equals(draft)) content.set(draft);
        if (this.postId.get() != -1) content.set(String.format(Locale.ENGLISH, "[quote=%d:@%s]%s[/quote]\n", this.postId.get(), this.replyTo.get(), this.replyMsg.get()));
        selection.set(content.get().length());
    }

    public void reply() {
        PostUtils.reply(conversationId.get(), content.get(), new PostUtils.ReplyObserver()
        {
            @Override
            public void onReplySuccess()
            {
                showToast.notifyChange();
                toastContent.set(getString(R.string.reply));
                clearSaved();
                replyComplete.set(true);
            }

            @Override
            public void onReplyFailure(int statusCode)
            {
                showToast.notifyChange();
                toastContent.set("Fail: " + statusCode);
            }
        });
    }

    public void edit() {
        PostUtils.edit(postId.get(), content.get(), new PostUtils.EditObserver()
        {
            @Override
            public void onEditSuccess()
            {
                showToast.set(showToast.get() + 1);
                toastContent.set("Post"); // TODO: 16-10-11
                editComplete.set(true);
            }

            @Override
            public void onEditFailure(int statusCode)
            {
                showToast.set(showToast.get() + 1);
                toastContent.set("Fail:" + statusCode); // TODO: 16-10-11
            }
        });
    }
    public void saveReply() {
        e.putString(String.valueOf(conversationId.get()), content.get()).apply();
    }

    public void clearSaved() {
        e.clear().apply();
    }
}

