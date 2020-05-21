package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class GiftRecord extends BaseObservable {
    long friendDynamicGiftId;  //详情id
    String friendDynamicText = ""; //动态内容
    String giftName = "";    //礼物名称
    String giftImage = "";     //礼物图片
    int giftNumber;        //礼物数量
    String nick = "";
    String image = "";
    String createTime = "";

    @Bindable
    public long getFriendDynamicGiftId() {
        return friendDynamicGiftId;
    }

    public void setFriendDynamicGiftId(long friendDynamicGiftId) {
        this.friendDynamicGiftId = friendDynamicGiftId;
        notifyPropertyChanged(BR.friendDynamicGiftId);
    }

    @Bindable public String getFriendDynamicText() {
        return friendDynamicText;
    }

    public void setFriendDynamicText(String friendDynamicText) {
        this.friendDynamicText = friendDynamicText;
        notifyPropertyChanged(BR.friendDynamicText);
    }

    @Bindable public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
        notifyPropertyChanged(BR.giftName);
    }

    @Bindable public String getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(String giftImage) {
        this.giftImage = giftImage;
        notifyPropertyChanged(BR.giftImage);
    }

    @Bindable public int getGiftNumber() {
        return giftNumber;
    }

    public void setGiftNumber(int giftNumber) {
        this.giftNumber = giftNumber;
        notifyPropertyChanged(BR.giftNumber);
    }

    @Bindable public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }
}
