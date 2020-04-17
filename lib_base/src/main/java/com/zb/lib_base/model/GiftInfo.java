package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class GiftInfo extends BaseObservable {
    private String giftName = "";//礼物名称
    private String giftImage = "";//礼物图片
    private double payMoney = 0;//支付金额 [所需的币]

    @Bindable
    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
        notifyPropertyChanged(BR.giftName);
    }

    @Bindable public String getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(String giftImage) {
        this.giftImage = giftImage;
        notifyPropertyChanged(BR.giftImage);
    }

    @Bindable public double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(double payMoney) {
        this.payMoney = payMoney;
        notifyPropertyChanged(BR.payMoney);
    }
}
