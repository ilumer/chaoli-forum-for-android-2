package com.geno.chaoli.forum.viewmodel;

import android.content.Intent;
import android.os.Bundle;

import com.geno.chaoli.forum.data.Me;
import com.geno.chaoli.forum.meta.Channel;
import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.model.Conversation;
import com.geno.chaoli.forum.view.HomepageActivity;
import com.geno.chaoli.forum.view.LoginActivity;

/**
 * Created by jianhao on 16-9-19.
 */
public class NavigationViewModel extends BaseViewModel {
    public String getChannelByPosition(int position) {
        String[] channel =
                new String[]
                        {
                                "",
                                Channel.caff.name(),
                                Channel.maths.name(),
                                Channel.physics.name(),
                                Channel.chem.name(),
                                Channel.biology.name(),
                                Channel.tech.name(),
                                Channel.court.name(),
                                Channel.announ.name(),
                                Channel.others.name(),
                                Channel.socsci.name(),
                                Channel.lang.name(),
                        };
        return channel[position];
    }

}
