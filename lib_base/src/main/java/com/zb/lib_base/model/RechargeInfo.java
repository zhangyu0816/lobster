package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class RechargeInfo extends BaseObservable {
    double price = 10;
    int rechargeValue = 10;
    String priceDesc = "";

    @Bindable
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public int getRechargeValue() {
        return rechargeValue;
    }

    public void setRechargeValue(int rechargeValue) {
        this.rechargeValue = rechargeValue;
        notifyPropertyChanged(BR.rechargeValue);
    }

    @Bindable
    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
        notifyPropertyChanged(BR.priceDesc);
    }
}
