package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class RecommendInfo extends BaseObservable {
    private long userId;
    private String userImage = "";
    private String userNick = "";
    private int age;
    private int sex;
    private int cityId;
    private String personalitySign = "";
    private String singleImage = "";

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
        notifyPropertyChanged(BR.userImage);
    }

    @Bindable
    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
        notifyPropertyChanged(BR.userNick);
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
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }

    @Bindable
    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
        notifyPropertyChanged(BR.cityId);
    }

    @Bindable
    public String getPersonalitySign() {
        return personalitySign;
    }

    public void setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
        notifyPropertyChanged(BR.personalitySign);
    }
    @Bindable
    public String getSingleImage() {
        return singleImage;
    }

    public void setSingleImage(String singleImage) {
        this.singleImage = singleImage;
        notifyPropertyChanged(BR.singleImage);
    }
}
