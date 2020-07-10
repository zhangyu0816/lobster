package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class BottleInfo extends BaseObservable {
    long driftBottleId;//漂流瓶id
    long userId;
    String text = ""; //内容
    int driftBottleType; //漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
    int noReadNum;//未读数量
    String otherHeadImage = "";//头像
    String otherNick = "";  //昵称
    long otherUserId; //
    String modifyTime = "";

    @Bindable
    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        notifyPropertyChanged(BR.driftBottleId);
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
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable
    public int getDriftBottleType() {
        return driftBottleType;
    }

    public void setDriftBottleType(int driftBottleType) {
        this.driftBottleType = driftBottleType;
        notifyPropertyChanged(BR.driftBottleType);
    }

    @Bindable
    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable
    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
        notifyPropertyChanged(BR.noReadNum);
    }

    @Bindable
    public String getOtherHeadImage() {
        return otherHeadImage;
    }

    public void setOtherHeadImage(String otherHeadImage) {
        this.otherHeadImage = otherHeadImage;
        notifyPropertyChanged(BR.otherHeadImage);
    }

    @Bindable
    public String getOtherNick() {
        return otherNick;
    }

    public void setOtherNick(String otherNick) {
        this.otherNick = otherNick;
        notifyPropertyChanged(BR.otherNick);
    }

    @Bindable
    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
        notifyPropertyChanged(BR.modifyTime);
    }
}
