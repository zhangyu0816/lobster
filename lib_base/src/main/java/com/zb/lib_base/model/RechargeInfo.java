package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class RechargeInfo extends BaseObservable {
    long id;
    double originalMoney;    //原价 金额
    double extraGiveMoney;    //额外赠送 金额
    int moneyType;        // 0.默认   1.最受欢迎  2.优惠最大  3.........
    String content = "";

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public double getOriginalMoney() {
        return originalMoney;
    }

    public void setOriginalMoney(double originalMoney) {
        this.originalMoney = originalMoney;
        notifyPropertyChanged(BR.originalMoney);
    }

    @Bindable
    public double getExtraGiveMoney() {
        return extraGiveMoney;
    }

    public void setExtraGiveMoney(double extraGiveMoney) {
        this.extraGiveMoney = extraGiveMoney;
        notifyPropertyChanged(BR.extraGiveMoney);
    }

    @Bindable
    public int getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(int moneyType) {
        this.moneyType = moneyType;
        notifyPropertyChanged(BR.moneyType);
    }

    @Bindable
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }
}
