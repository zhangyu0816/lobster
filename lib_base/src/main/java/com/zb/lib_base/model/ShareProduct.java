package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ShareProduct extends BaseObservable {
    long id;
    String title = ""; //标题
    String image = ""; //图片
    String simpleDesc = "";  //简单描述
    String fullDesc = ""; //详细
    double originalPrice; //原价
    double price; //当前价格
    String priceDesc = ""; //当前价格描述
    int markeType; // 1.合伙人  2.??
    int indexNo; //排序号

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable public String getSimpleDesc() {
        return simpleDesc;
    }

    public void setSimpleDesc(String simpleDesc) {
        this.simpleDesc = simpleDesc;
        notifyPropertyChanged(BR.simpleDesc);
    }

    @Bindable public String getFullDesc() {
        return fullDesc;
    }

    public void setFullDesc(String fullDesc) {
        this.fullDesc = fullDesc;
        notifyPropertyChanged(BR.fullDesc);
    }

    @Bindable public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
        notifyPropertyChanged(BR.originalPrice);
    }

    @Bindable public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
        notifyPropertyChanged(BR.priceDesc);
    }

    @Bindable public int getMarkeType() {
        return markeType;
    }

    public void setMarkeType(int markeType) {
        this.markeType = markeType;
        notifyPropertyChanged(BR.markeType);
    }

    @Bindable public int getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(int indexNo) {
        this.indexNo = indexNo;
        notifyPropertyChanged(BR.indexNo);
    }
}
