package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MineNewsCount extends BaseObservable {
    int friendDynamicReviewNum; //未读评论数量
    int friendDynamicGiftNum;   //未读收礼数量
    int friendDynamicGoodNum;  //未读点赞数量
    int systemNewsNum;
    String content = "";
    String createTime = "";
    int msgType;//消息类型 1：文字 2：图片 3：语音 4：视频

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

    @Bindable
    public int getSystemNewsNum() {
        return systemNewsNum;
    }

    public void setSystemNewsNum(int systemNewsNum) {
        this.systemNewsNum = systemNewsNum;
        notifyPropertyChanged(BR.systemNewsNum);
    }

    @Bindable
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
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
    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
        notifyPropertyChanged(BR.msgType);
    }
}
