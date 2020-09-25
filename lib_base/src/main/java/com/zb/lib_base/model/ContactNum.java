package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ContactNum extends BaseObservable {
    long userId;     //
    int fansCount;//粉丝数量
    int concernCount; //关注数量
    int beLikeCount; //被喜欢数量
    int likeCount; //喜欢数量
    int beSuperLikeCount;// 被超级喜欢
    int visitorCount;

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
        notifyPropertyChanged(BR.fansCount);
    }

    @Bindable
    public int getConcernCount() {
        return concernCount;
    }

    public void setConcernCount(int concernCount) {
        this.concernCount = concernCount;
        notifyPropertyChanged(BR.concernCount);
    }

    @Bindable
    public int getBeLikeCount() {
        return beLikeCount;
    }

    public void setBeLikeCount(int beLikeCount) {
        this.beLikeCount = beLikeCount;
        notifyPropertyChanged(BR.beLikeCount);
    }

    @Bindable
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        notifyPropertyChanged(BR.likeCount);
    }

    @Bindable
    public int getBeSuperLikeCount() {
        return beSuperLikeCount;
    }

    public void setBeSuperLikeCount(int beSuperLikeCount) {
        this.beSuperLikeCount = beSuperLikeCount;
        notifyPropertyChanged(BR.beSuperLikeCount);
    }

    @Bindable
    public int getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(int visitorCount) {
        this.visitorCount = visitorCount;
        notifyPropertyChanged(BR.visitorCount);
    }
}
