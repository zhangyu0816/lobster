package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class RentInfo extends BaseObservable {

    private long userId;
    private String price = "";
    private String timeScope = "";// 时间范围
    private String serviceTags = "";// 服务范围说明
    private String serviceScope = "";// 服务范围说明
    private String weixinNum = "";//
    private String phoneNum = ""; // (这就是绑定的手机号码)
    private int imgVerifyStatus;// 0审核 1通过 -1 未通过
    private int hasWeixinNum = 0;

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable public String getTimeScope() {
        return timeScope;
    }

    public void setTimeScope(String timeScope) {
        this.timeScope = timeScope;
        notifyPropertyChanged(BR.timeScope);
    }

    @Bindable public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        notifyPropertyChanged(BR.serviceTags);
    }

    @Bindable public String getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(String serviceScope) {
        this.serviceScope = serviceScope;
        notifyPropertyChanged(BR.serviceScope);
    }

    @Bindable public String getWeixinNum() {
        return weixinNum;
    }

    public void setWeixinNum(String weixinNum) {
        this.weixinNum = weixinNum;
        notifyPropertyChanged(BR.weixinNum);
    }

    @Bindable public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        notifyPropertyChanged(BR.phoneNum);
    }

    @Bindable public int getImgVerifyStatus() {
        return imgVerifyStatus;
    }

    public void setImgVerifyStatus(int imgVerifyStatus) {
        this.imgVerifyStatus = imgVerifyStatus;
        notifyPropertyChanged(BR.imgVerifyStatus);
    }

    @Bindable public int getHasWeixinNum() {
        return hasWeixinNum;
    }

    public void setHasWeixinNum(int hasWeixinNum) {
        this.hasWeixinNum = hasWeixinNum;
        notifyPropertyChanged(BR.hasWeixinNum);
    }
}
