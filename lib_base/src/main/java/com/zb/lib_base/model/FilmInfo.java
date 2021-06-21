package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FilmInfo extends BaseObservable {
    long id;
    long userId;
    long cameraFilmId;//属于哪个胶卷
    String image = "";
    String videoUrl = "";//视频地址
    int resTime; //资源时长  秒Member_myInfo
    int goodNum;//赞的数量
    int reviews;//评论数
    String nick = "";    //用户昵称
    String headImage = "";//用户头像

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
    public long getCameraFilmId() {
        return cameraFilmId;
    }

    public void setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        notifyPropertyChanged(BR.cameraFilmId);
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
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        notifyPropertyChanged(BR.videoUrl);
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
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable
    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
        notifyPropertyChanged(BR.headImage);
    }
}
