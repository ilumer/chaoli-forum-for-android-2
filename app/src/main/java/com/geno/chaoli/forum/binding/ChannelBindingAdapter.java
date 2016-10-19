package com.geno.chaoli.forum.binding;

import android.databinding.BindingAdapter;

import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.ChannelTextView;

/**
 * Created by jianhao on 16-9-25.
 */

public class ChannelBindingAdapter {
    @BindingAdapter("app:channelId")
    public static void setChannel(ChannelTextView channelTextView, int channelId) {
        channelTextView.setChannel(Channel.getChannel(channelId));
    }
}
