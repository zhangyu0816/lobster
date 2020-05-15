package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class DiscoverInfo extends BaseObservable {

    private long friendDynId = 0L;
    private long otherUserId = 0L;
    private String nick = "";// 昵称 艺名//1-16
    private String image = "";// 头像
    private String text = "";
    private String images = "";
    private int imageSize = 0; // 图片数量
    private int dycType = 0; // 未知 0文字 1图片 2 图文 3 视频 4 视频_文字 5 视频_图片 6 视频_图片_文字 7  问题 视频_图片 22  问题 视频_图片_文字 23
    private String videoUrl = ""; // 视频地址
    private String createTime = "2020-04-16 09:41:00";
    private int isDoGood = 0; // 是否已经点赞//1已经点赞 0未点赞
    private int pageviews = 0;// 查看数
    private int goodNum = 0;// 赞的数量
    private int reviews = 0;// 评论数
    private int rewardNum = 0; //打赏数量
    private int resTime = 0;
    private int privateRedPageNum = 0; //私密红包数量
    private String friendTitle = "";


    @Bindable
    public long getFriendDynId() {
        return friendDynId;
    }

    public void setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        notifyPropertyChanged(BR.friendDynId);
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
    public int getPrivateRedPageNum() {
        return privateRedPageNum;
    }

    public void setPrivateRedPageNum(int privateRedPageNum) {
        this.privateRedPageNum = privateRedPageNum;
        notifyPropertyChanged(BR.privateRedPageNum);
    }

    @Bindable
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
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
    public int getDycType() {
        return dycType;
    }

    public void setDycType(int dycType) {
        this.dycType = dycType;
        notifyPropertyChanged(BR.dycType);
    }

    @Bindable
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        notifyPropertyChanged(BR.videoUrl);
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
    public int getIsDoGood() {
        return isDoGood;
    }

    public void setIsDoGood(int isDoGood) {
        this.isDoGood = isDoGood;
        notifyPropertyChanged(BR.isDoGood);
    }

    @Bindable
    public int getPageviews() {
        return pageviews;
    }

    public void setPageviews(int pageviews) {
        this.pageviews = pageviews;
        notifyPropertyChanged(BR.pageviews);
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
    public int getRewardNum() {
        return rewardNum;
    }

    public void setRewardNum(int rewardNum) {
        this.rewardNum = rewardNum;
        notifyPropertyChanged(BR.rewardNum);
    }

    @Bindable
    public int getResTime() {
        return resTime;
    }

    public void setResTime(int resTime) {
        this.resTime = resTime;
        notifyPropertyChanged(BR.resTime);
    }

    @Bindable
    public String getFriendTitle() {
        return friendTitle;
    }

    public void setFriendTitle(String friendTitle) {
        this.friendTitle = friendTitle;
        notifyPropertyChanged(BR.friendTitle);
    }
}
