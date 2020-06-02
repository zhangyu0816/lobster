package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AttentionInfo extends RealmObject {
    @PrimaryKey
    private long otherUserId;
    private String nick = "";
    private String image = "";
    private boolean isAttention = false;
    private long mainUserId;// 拥有人

    public AttentionInfo() {
    }

    public AttentionInfo(long otherUserId, String nick, String image, boolean isAttention, long mainUserId) {
        this.otherUserId = otherUserId;
        this.nick = nick;
        this.image = image;
        this.isAttention = isAttention;
        this.mainUserId = mainUserId;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAttention() {
        return isAttention;
    }

    public void setAttention(boolean attention) {
        isAttention = attention;
    }

    public long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(long mainUserId) {
        this.mainUserId = mainUserId;
    }
}
