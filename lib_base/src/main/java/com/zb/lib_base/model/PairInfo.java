package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class PairInfo extends BaseObservable {
    private long userId;  //用户id  预匹配列表中是对方的id
    private long otherUserId; //对方用户的id
    private String nick = ""; //昵称
    private String moreImages = "";//多图
    private String personalitySign = ""; //个性签名
    private String birthday = "1988-08-16";
    private int age;//年龄
    private int sex;//性别  0女  1男
    private int idAttest;//是否实名认证  0未认证  1认证
    private long provinceId = 11;  //省份id
    private long cityId = 89;   //地区id
    private long districtId = 858;
    private String distance = "1000";
    private List<String> imageList = new ArrayList<>();

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable public String getMoreImages() {
        return moreImages;
    }

    public void setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        notifyPropertyChanged(BR.moreImages);
    }

    @Bindable public String getPersonalitySign() {
        return personalitySign;
    }

    public void setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
        notifyPropertyChanged(BR.personalitySign);
    }

    @Bindable public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        notifyPropertyChanged(BR.birthday);
    }

    @Bindable public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    @Bindable public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }

    @Bindable public int getIdAttest() {
        return idAttest;
    }

    public void setIdAttest(int idAttest) {
        this.idAttest = idAttest;
        notifyPropertyChanged(BR.idAttest);
    }

    @Bindable public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        notifyPropertyChanged(BR.provinceId);
    }

    @Bindable public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
        notifyPropertyChanged(BR.cityId);
    }

    @Bindable public long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(long districtId) {
        this.districtId = districtId;
        notifyPropertyChanged(BR.districtId);
    }

    @Bindable public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        notifyPropertyChanged(BR.imageList);
    }
}
