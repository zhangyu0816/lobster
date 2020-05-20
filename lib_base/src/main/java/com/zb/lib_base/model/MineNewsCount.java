package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MineNewsCount extends BaseObservable {
    int friendDynamicReviewNum; //未读评论数量
    int friendDynamicGiftNum;   //未读收礼数量
    int friendDynamicGoodNum;  //未读点赞数量

    @Bindable
    public int getFriendDynamicReviewNum() {
        return friendDynamicReviewNum;
    }

    public void setFriendDynamicReviewNum(int friendDynamicReviewNum) {
        this.friendDynamicReviewNum = friendDynamicReviewNum;
        notifyPropertyChanged(BR.friendDynamicReviewNum);
    }

    @Bindable
    public int getFriendDynamicGiftNum() {
        return friendDynamicGiftNum;
    }

    public void setFriendDynamicGiftNum(int friendDynamicGiftNum) {
        this.friendDynamicGiftNum = friendDynamicGiftNum;
        notifyPropertyChanged(BR.friendDynamicGiftNum);
    }

    @Bindable
    public int getFriendDynamicGoodNum() {
        return friendDynamicGoodNum;
    }

    public void setFriendDynamicGoodNum(int friendDynamicGoodNum) {
        this.friendDynamicGoodNum = friendDynamicGoodNum;
        notifyPropertyChanged(BR.friendDynamicGoodNum);
    }
}
