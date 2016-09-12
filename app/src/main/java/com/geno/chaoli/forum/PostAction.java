package com.geno.chaoli.forum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.ChannelTextView;
import com.geno.chaoli.forum.utils.ConversationUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jianhao on 16-5-31.
 */
public class PostAction extends BaseActivity implements ConversationUtils.AddMemberObserver, ConversationUtils.GetMembersAllowedObserver,
        ConversationUtils.PostConversationObserver, ConversationUtils.RemoveMemberObserver, ConversationUtils.SetChannelObserver{

    private static final String TAG = "PostAction";

    //public static final int MENU_DRAFT = 0;
    public static final int MENU_POST = 1;
    //public static final int MENU_PURGE = 2;

    private static final String DRAFT_CONTENT = "draft_content";
    private static final String DRAFT_TITLE = "draft_title";
    private static final String DRAFT_CHANNEL = "draft_channel";

    private Channel preChannel, curChannel;

    @BindView(R.id.channel)
    LinearLayout channel;
    @BindView(R.id.title)
    EditText title_et;
    @BindView(R.id.content)
    EditText content_et;

    private final Context mContext = this;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_action);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
        configToolbar(R.string.post);

        sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final String[] channelArr = {getString(R.string.channel_caff), getString(R.string.channel_maths), getString(R.string.channel_physics),
                getString(R.string.channel_biology),getString(R.string.channel_tech), getString(R.string.channel_lang),
                getString(R.string.channel_socsci)};

        String title = sharedPreferences.getString(DRAFT_TITLE, "");
        String content = sharedPreferences.getString(DRAFT_CONTENT, "");
        String channelText = sharedPreferences.getString(DRAFT_CHANNEL, getString(R.string.channel_caff));
        curChannel = Channel.getChannel(mContext, channelText);

        title_et.setText(title);
        content_et.setText(content);
        title_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString(DRAFT_TITLE, editable.toString()).apply();
            }
        });
        content_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString(DRAFT_CONTENT, editable.toString()).apply();
            }
        });
        channel.addView(new ChannelTextView(this, Channel.getChannel(mContext, channelText)));
        channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext).setTitle("选择板块").setItems(channelArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preChannel = curChannel;
                        curChannel = Channel.getChannel(mContext, channelArr[which]);
                        ConversationUtils.setChannel(mContext, curChannel.getChannelId(), (ConversationUtils.SetChannelObserver) mContext);
                        channel.removeAllViews();
                        channel.addView(new ChannelTextView(mContext, Channel.getChannel(mContext, channelArr[which])));
                        editor.putString(DRAFT_CHANNEL, curChannel.toString(mContext)).apply();
                    }
                }).setCancelable(false).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //menu.add(Menu.NONE, Menu.NONE, MENU_DRAFT, R.string.save_as_draft).setIcon(android.R.drawable.ic_menu_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(Menu.NONE, Menu.NONE, MENU_POST, R.string.post).setIcon(R.drawable.ic_cab_done_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(Menu.NONE, Menu.NONE, MENU_PURGE, R.string.purge).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        String title = title_et.getText().toString();
        String content = content_et.getText().toString();
        switch (item.getOrder())
        {
            /*case MENU_DRAFT:
                editor.putString(DRAFT_TITLE, title);
                editor.putString(DRAFT_CONTENT, content);
                editor.putString(DRAFT_CHANNEL, curChannel.toString(mContext));
                editor.apply();
                Toast.makeText(this, R.string.save_as_draft, Toast.LENGTH_SHORT).show();
                finish();
                break;
                */
            case MENU_POST:
                ConversationUtils.postConversation(this, title, content, this);
                break;
            /*
            case MENU_PURGE:
                editor.clear().apply();
                break;
                */
        }
        return true;
    }

    @Override
    public void onPostConversationSuccess(int conversationId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        Intent intent = new Intent(PostAction.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onAddMemberFailure(int statusCode) {
        Toast.makeText(this, getString(R.string.network_err), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddMemberSuccess() {

    }

    @Override
    public void onGetMembersAllowedFailure(int statusCode) {
        Toast.makeText(this, getString(R.string.network_err), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetMembersAllowedSuccess(List<Integer> memberList) {

    }

    @Override
    public void onPostConversationFailure(int statusCode) {
        Toast.makeText(this, getString(R.string.network_err), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRemoveMemberFailure(int statusCode) {
        Toast.makeText(this, getString(R.string.network_err), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRemoveMemberSuccess() {

    }

    @Override
    public void onSetChannelFailure(int statusCode) {
        Toast.makeText(this, getString(R.string.network_err) + statusCode, Toast.LENGTH_LONG).show();
        channel.removeAllViews();
        channel.addView(new ChannelTextView(this, preChannel));
        curChannel = preChannel;
    }

    @Override
    public void onSetChannelSuccess() {

    }
}
