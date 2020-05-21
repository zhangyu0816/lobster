package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class BottleMsg extends BaseObservable {
    long driftBottleId;   //所属漂流瓶
    long userId;// 回复者id
    long forUserId;// 对方的userId
    int isRead;//是否读取 1读取 0未读
    int isPass; //是否通过审核 1通过 0不通过
    String text = ""; //回复内容

    @Bindable
    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        notifyPropertyChanged(BR.driftBottleId);
    }

    @Bindable public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public long getForUserId() {
        return forUserId;
    }

    public void setForUserId(long forUserId) {
        this.forUserId = forUserId;
        notifyPropertyChanged(BR.forUserId);
    }

    @Bindable public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
        notifyPropertyChanged(BR.isRead);
    }

    @Bindable public int getIsPass() {
        return isPass;
    }

    public void setIsPass(int isPass) {
        this.isPass = isPass;
        notifyPropertyChanged(BR.isPass);
    }

    @Bindable public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
}
