package com.geno.chaoli.forum.model;

import com.geno.chaoli.forum.HomepageListFragment;

/**
 * Created by jianhao on 16-9-3.
 */
public class HistoryItem extends HomepageListFragment.ListItem {
    String start;
    String postId;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    String content;
    String title;
    String description;
    String data;

    private static class Data{
        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public String getNewSignature() {
            return newSignature;
        }

        public void setNewSignature(String newSignature) {
            this.newSignature = newSignature;
        }

        String newStatus;
        String newSignature;
    }
}
