package com.zb.lib_base.model;


import com.zb.lib_base.BR;

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
    String openId = "";
    String unionId = "";
    int unionType;
    String unionImage = "";
    String job = "";
    String personalitySign = "";//个性签名
    String serviceTags = "";
    String image = "";

    String bindPhone = "";

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
    public String getBindPhone() {
        return bindPhone;
    }

    public void setBindPhone(String bindPhone) {
        this.bindPhone = bindPhone;
        notifyPropertyChanged(BR.bindPhone);
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
    public String getUnionImage() {
        return unionImage;
    }

    public void setUnionImage(String unionImage) {
        this.unionImage = unionImage;
        notifyPropertyChanged(BR.unionImage);
    }

    @Bindable
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
        notifyPropertyChanged(BR.job);
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
    public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        notifyPropertyChanged(BR.serviceTags);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }
}
