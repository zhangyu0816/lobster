package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class GiftRecord extends BaseObservable {
    long friendDynamicGiftId;  //详情id
    long friendDynamicId;
    String friendDynamicText = ""; //动态内容
    String giftName = "";    //礼物名称
    String giftImage = "";     //礼物图片
    int giftNumber;        //礼物数量
    String givingUserNick = "";
    String givingUserHeadImage = "";
    String createTime = "";
    long givingUserId;
    int dycType;

    @Bindable
    public long getFriendDynamicGiftId() {
        return friendDynamicGiftId;
    }

    public void setFriendDynamicGiftId(long friendDynamicGiftId) {
        this.friendDynamicGiftId = friendDynamicGiftId;
        notifyPropertyChanged(BR.friendDynamicGiftId);
    }

    @Bindable
    public long getFriendDynamicId() {
        return friendDynamicId;
    }

    public void setFriendDynamicId(long friendDynamicId) {
        this.friendDynamicId = friendDynamicId;
        notifyPropertyChanged(BR.friendDynamicId);
    }

    @Bindable
    public String getFriendDynamicText() {
        return friendDynamicText;
    }

    public void setFriendDynamicText(String friendDynamicText) {
        this.friendDynamicText = friendDynamicText;
        notifyPropertyChanged(BR.friendDynamicText);
    }

    @Bindable
    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
        notifyPropertyChanged(BR.giftName);
    }

    @Bindable
    public String getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(String giftImage) {
        this.giftImage = giftImage;
        notifyPropertyChanged(BR.giftImage);
    }

    @Bindable
    public int getGiftNumber() {
        return giftNumber;
    }

    public void setGiftNumber(int giftNumber) {
        this.giftNumber = giftNumber;
        notifyPropertyChanged(BR.giftNumber);
    }

    @Bindable
    public String getGivingUserNick() {
        return givingUserNick;
    }

    public void setGivingUserNick(String givingUserNick) {
        this.givingUserNick = givingUserNick;
        notifyPropertyChanged(BR.givingUserNick);
    }

    @Bindable
    public String getGivingUserHeadImage() {
        return givingUserHeadImage;
    }

    public void setGivingUserHeadImage(String givingUserHeadImage) {
        this.givingUserHeadImage = givingUserHeadImage;
        notifyPropertyChanged(BR.givingUserHeadImage);
    }

    @Bindable
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable
    public long getGivingUserId() {
        return givingUserId;
    }

    public void setGivingUserId(long givingUserId) {
        this.givingUserId = givingUserId;
        notifyPropertyChanged(BR.givingUserId);
    }

    @Bindable
    public int getDycType() {
        return dycType;
    }

    public void setDycType(int dycType) {
        this.dycType = dycType;
        notifyPropertyChanged(BR.dycType);
    }
}
