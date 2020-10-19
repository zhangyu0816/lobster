package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import java.io.Serializable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FlashInfo extends BaseObservable implements Serializable {
    private long userId; //推荐用户id
    private String nick; //昵称
    private String singleImage;//形象图
    private String lastJoinTime; //活跃时间
    private String birthday = "";    //生日
    private int age;        //年龄
    private String serviceTags = "";
    private int sex;

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
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
    public String getSingleImage() {
        return singleImage;
    }

    public void setSingleImage(String singleImage) {
        this.singleImage = singleImage;
        notifyPropertyChanged(BR.singleImage);
    }

    @Bindable
    public String getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(String lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
        notifyPropertyChanged(BR.lastJoinTime);
    }

    @Bindable
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        notifyPropertyChanged(BR.birthday);
    }

    @Bindable
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    @Bindable
    public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        notifyPropertyChanged(BR.serviceTags);
    }

    @Bindable
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }
}
