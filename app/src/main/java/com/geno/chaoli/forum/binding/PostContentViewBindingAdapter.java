package com.geno.chaoli.forum.binding;

import android.databinding.BindingAdapter;

import com.geno.chaoli.forum.meta.PostContentView;
import com.geno.chaoli.forum.model.Post;

/**
 * Created by jianhao on 16-9-27.
 */

public class PostContentViewBindingAdapter {
    @BindingAdapter("app:post")
    public static void setPost(PostContentView postContentView, Post post) {
        postContentView.setPost(post);
    }
}
