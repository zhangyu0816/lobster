package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Film extends BaseObservable {
    long id;//胶卷id
    long userId;
    String title = "";
    String text = "";
    String images = "";//封面
    int imageSize;
    int camerafilmType;//胶卷类型
    String washTime = "";//冲洗完成 时间
    int washSetHourTime;//冲洗所需时间[小时为单位]
    int goodNum;//赞的数量
    int reviews;//评论数
    int shareNum;//分享数量
    int authority;//权限设置 1 公开 2 仅好友可见 3.私密
    int isEnable; //是否可用   1.可用  0.不可用
    int washType;// 0:未冲洗  1：冲洗中  2：冲洗完

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
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
    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Bindable
    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
        notifyPropertyChanged(BR.imageSize);
    }

    @Bindable
    public int getCamerafilmType() {
        return camerafilmType;
    }

    public void setCamerafilmType(int camerafilmType) {
        this.camerafilmType = camerafilmType;
        notifyPropertyChanged(BR.camerafilmType);
    }

    @Bindable
    public String getWashTime() {
        return washTime;
    }

    public void setWashTime(String washTime) {
        this.washTime = washTime;
        notifyPropertyChanged(BR.washTime);
    }

    @Bindable
    public int getWashSetHourTime() {
        return washSetHourTime;
    }

    public void setWashSetHourTime(int washSetHourTime) {
        this.washSetHourTime = washSetHourTime;
        notifyPropertyChanged(BR.washSetHourTime);
    }

    @Bindable
    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
        notifyPropertyChanged(BR.goodNum);
    }

    @Bindable
    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
        notifyPropertyChanged(BR.reviews);
    }

    @Bindable
    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
        notifyPropertyChanged(BR.shareNum);
    }

    @Bindable
    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
        notifyPropertyChanged(BR.authority);
    }

    @Bindable
    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
        notifyPropertyChanged(BR.isEnable);
    }

    @Bindable
    public int getWashType() {
        return washType;
    }

    public void setWashType(int washType) {
        this.washType = washType;
        notifyPropertyChanged(BR.washType);
    }
}
