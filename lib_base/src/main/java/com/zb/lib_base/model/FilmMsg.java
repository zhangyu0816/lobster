package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FilmMsg extends BaseObservable {
    long id;//cameraFilmResourceReviewMsgId
    long forUserId;//消息接受
    long cameraFilmResourceId;
    long reviewUserId;//评论者的UserId
    int reviewType;//1评论  2.点赞
    String text = "";//评论的消息
    String createTime = "";
    int isRead;//是否读取 1.已读  0.未读
    String reviewUserNick = "";       //昵称
    String reviewUserHeadImage = "";      //头像

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable public long getForUserId() {
        return forUserId;
    }

    public void setForUserId(long forUserId) {
        this.forUserId = forUserId;
        notifyPropertyChanged(BR.forUserId);
    }

    @Bindable public long getCameraFilmResourceId() {
        return cameraFilmResourceId;
    }

    public void setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        notifyPropertyChanged(BR.cameraFilmResourceId);
    }

    @Bindable public long getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(long reviewUserId) {
        this.reviewUserId = reviewUserId;
        notifyPropertyChanged(BR.reviewUserId);
    }

    @Bindable public int getReviewType() {
        return reviewType;
    }

    public void setReviewType(int reviewType) {
        this.reviewType = reviewType;
        notifyPropertyChanged(BR.reviewType);
    }

    @Bindable public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
        notifyPropertyChanged(BR.isRead);
    }

    @Bindable public String getReviewUserNick() {
        return reviewUserNick;
    }

    public void setReviewUserNick(String reviewUserNick) {
        this.reviewUserNick = reviewUserNick;
        notifyPropertyChanged(BR.reviewUserNick);
    }

    @Bindable public String getReviewUserHeadImage() {
        return reviewUserHeadImage;
    }

    public void setReviewUserHeadImage(String reviewUserHeadImage) {
        this.reviewUserHeadImage = reviewUserHeadImage;
        notifyPropertyChanged(BR.reviewUserHeadImage);
    }
}
