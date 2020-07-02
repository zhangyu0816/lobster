package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MineNews extends BaseObservable {
    long reviewMsgId;//消息id

    //动态的信息
    long friendDynamicId;
    String friendDynamicText = "";
    String friendDynamicImages = "";
    int friendDynamicImageSize;
    String friendDynamicVideoUrl = "";
    String friendDynamicTitle = "";
    int friendDynamicDycType;
    //    未知   0
//    文字   1
//    图片   2
//    图文   3
//    视频   4
//    视频_文字  5
//    视频_图片  6
//    视频_图片_文字   7
    long reviewUserId; //评论者的UserId
    String reviewUserNick = "";
    String reviewUserImage = "";

    int reviewType; //1评论  2.点赞 3.礼物
    String text = ""; //评论的消息
    String createTime = "";  //消息时间
    long friendDynamicGiftId;  //赠礼记录id
    long giftNumber;        //数量
    String giftImage = "";        //礼物图片
    String giftName = "";        //礼物图片

    @Bindable
    public long getReviewMsgId() {
        return reviewMsgId;
    }

    public void setReviewMsgId(long reviewMsgId) {
        this.reviewMsgId = reviewMsgId;
        notifyPropertyChanged(BR.reviewMsgId);
    }

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
    public String getFriendDynamicImages() {
        return friendDynamicImages;
    }

    public void setFriendDynamicImages(String friendDynamicImages) {
        this.friendDynamicImages = friendDynamicImages;
        notifyPropertyChanged(BR.friendDynamicImages);
    }

    @Bindable
    public int getFriendDynamicImageSize() {
        return friendDynamicImageSize;
    }

    public void setFriendDynamicImageSize(int friendDynamicImageSize) {
        this.friendDynamicImageSize = friendDynamicImageSize;
        notifyPropertyChanged(BR.friendDynamicImageSize);
    }

    @Bindable
    public String getFriendDynamicVideoUrl() {
        return friendDynamicVideoUrl;
    }

    public void setFriendDynamicVideoUrl(String friendDynamicVideoUrl) {
        this.friendDynamicVideoUrl = friendDynamicVideoUrl;
        notifyPropertyChanged(BR.friendDynamicVideoUrl);
    }

    @Bindable
    public int getFriendDynamicDycType() {
        return friendDynamicDycType;
    }

    public void setFriendDynamicDycType(int friendDynamicDycType) {
        this.friendDynamicDycType = friendDynamicDycType;
        notifyPropertyChanged(BR.friendDynamicDycType);
    }

    @Bindable
    public long getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(long reviewUserId) {
        this.reviewUserId = reviewUserId;
        notifyPropertyChanged(BR.reviewUserId);
    }

    @Bindable
    public String getReviewUserNick() {
        return reviewUserNick;
    }

    public void setReviewUserNick(String reviewUserNick) {
        this.reviewUserNick = reviewUserNick;
        notifyPropertyChanged(BR.reviewUserNick);
    }

    @Bindable
    public String getReviewUserImage() {
        return reviewUserImage;
    }

    public void setReviewUserImage(String reviewUserImage) {
        this.reviewUserImage = reviewUserImage;
        notifyPropertyChanged(BR.reviewUserImage);
    }

    @Bindable
    public int getReviewType() {
        return reviewType;
    }

    public void setReviewType(int reviewType) {
        this.reviewType = reviewType;
        notifyPropertyChanged(BR.reviewType);
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
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable
    public long getGiftNumber() {
        return giftNumber;
    }

    public void setGiftNumber(long giftNumber) {
        this.giftNumber = giftNumber;
        notifyPropertyChanged(BR.giftNumber);
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
    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
        notifyPropertyChanged(BR.giftName);
    }

    @Bindable
    public String getFriendDynamicTitle() {
        return friendDynamicTitle;
    }

    public void setFriendDynamicTitle(String friendDynamicTitle) {
        this.friendDynamicTitle = friendDynamicTitle;
        notifyPropertyChanged(BR.friendDynamicTitle);
    }
}
