package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class VipInfo extends BaseObservable {
    private long memberOfOpenedProductId;// 产品ID
    private String title = ""; // 标题
    private double originalPrice = 0; // 原价
    private double price = 0; // 当前价格
    private String priceDesc = ""; // 当前价格描述
    private int dayCount = 0; // 天数
    private String image = "";
    private String simpleDesc = "";

    @Bindable
    public long getMemberOfOpenedProductId() {
        return memberOfOpenedProductId;
    }

    public void setMemberOfOpenedProductId(long memberOfOpenedProductId) {
        this.memberOfOpenedProductId = memberOfOpenedProductId;
        notifyPropertyChanged(BR.memberOfOpenedProductId);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
        notifyPropertyChanged(BR.originalPrice);
    }

    @Bindable
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
        notifyPropertyChanged(BR.priceDesc);
    }

    @Bindable
    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
        notifyPropertyChanged(BR.dayCount);
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
    public String getSimpleDesc() {
        return simpleDesc;
    }

    public void setSimpleDesc(String simpleDesc) {
        this.simpleDesc = simpleDesc;
        notifyPropertyChanged(BR.simpleDesc);
    }
}
