package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Review extends BaseObservable {
    private long reviewId; // 评论id
    private long userId; // 评论者id
    private String nick = "";// 昵称
    private String image = "";// 头像
    private String text = "";// 评论的消息

    // 被at 人的信息
    private long atUserId = 0;
    private String atUserNick = "";
    private String atUserImage = "";
    private String createTime = "2020-04-16 15:55:00";

    @Bindable
    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
        notifyPropertyChanged(BR.reviewId);
    }

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable
    public long getAtUserId() {
        return atUserId;
    }

    public void setAtUserId(long atUserId) {
        this.atUserId = atUserId;
        notifyPropertyChanged(BR.atUserId);
    }

    @Bindable
    public String getAtUserNick() {
        return atUserNick;
    }

    public void setAtUserNick(String atUserNick) {
        this.atUserNick = atUserNick;
        notifyPropertyChanged(BR.atUserNick);
    }

    @Bindable
    public String getAtUserImage() {
        return atUserImage;
    }

    public void setAtUserImage(String atUserImage) {
        this.atUserImage = atUserImage;
        notifyPropertyChanged(BR.atUserImage);
    }

    @Bindable
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }
}
