package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FilmComment extends BaseObservable {
    long userId;
    long reviewId  ; //评论id
    String image="";
    String singleImage="";//形象图
    String nick="";
    String text="";//评论的消息
    long forReviewId;	//评论哪个id
    long atUserId;
    String atUserNick="";
    String atUserImage="";
    String atUserSingleImage="";
    String createTime="";

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
        notifyPropertyChanged(BR.reviewId);
    }

    @Bindable public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable public String getSingleImage() {
        return singleImage;
    }

    public void setSingleImage(String singleImage) {
        this.singleImage = singleImage;
        notifyPropertyChanged(BR.singleImage);
    }

    @Bindable public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable public long getForReviewId() {
        return forReviewId;
    }

    public void setForReviewId(long forReviewId) {
        this.forReviewId = forReviewId;
        notifyPropertyChanged(BR.forReviewId);
    }

    @Bindable public long getAtUserId() {
        return atUserId;
    }

    public void setAtUserId(long atUserId) {
        this.atUserId = atUserId;
        notifyPropertyChanged(BR.atUserId);
    }

    @Bindable public String getAtUserNick() {
        return atUserNick;
    }

    public void setAtUserNick(String atUserNick) {
        this.atUserNick = atUserNick;
        notifyPropertyChanged(BR.atUserNick);
    }

    @Bindable public String getAtUserImage() {
        return atUserImage;
    }

    public void setAtUserImage(String atUserImage) {
        this.atUserImage = atUserImage;
        notifyPropertyChanged(BR.atUserImage);
    }

    @Bindable public String getAtUserSingleImage() {
        return atUserSingleImage;
    }

    public void setAtUserSingleImage(String atUserSingleImage) {
        this.atUserSingleImage = atUserSingleImage;
        notifyPropertyChanged(BR.atUserSingleImage);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }
}
