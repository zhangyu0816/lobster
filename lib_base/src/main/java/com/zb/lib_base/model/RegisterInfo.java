package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class RegisterInfo extends BaseObservable {
    String name = "";
    int sex = 0;
    String phone = "";
    String birthday = "";
    String pass = "";
    String captcha = "";
    String moreImages = "";
    List<String> imageList = new ArrayList<>();

    String openId = "";
    String unionId = "";
    int unionType;

    @Bindable
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
        notifyPropertyChanged(BR.openId);
    }

    @Bindable
    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
        notifyPropertyChanged(BR.unionId);
    }

    @Bindable
    public int getUnionType() {
        return unionType;
    }

    public void setUnionType(int unionType) {
        this.unionType = unionType;
        notifyPropertyChanged(BR.unionType);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
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
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
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
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
        notifyPropertyChanged(BR.pass);
    }

    @Bindable
    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
        notifyPropertyChanged(BR.captcha);
    }

    @Bindable
    public String getMoreImages() {
        return moreImages;
    }

    public void setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        notifyPropertyChanged(BR.moreImages);
    }

    @Bindable
    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        notifyPropertyChanged(BR.imageList);
    }
}
