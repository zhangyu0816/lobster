package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class OrderTran extends BaseObservable {
    String tranOrderId = "";      //交易订单号
    String tranSimpleDesc = "";    //订单简单描述
    String tranDesc = "";       //订单详细描述
    double totalMoney;      //金额
    String alipayNotifyUrl = "";   //支付宝回调接口
    int canUseWallet;   //是否能够使用钱包支付 //1能使用  0不能

    @Bindable
    public String getTranOrderId() {
        return tranOrderId;
    }

    public void setTranOrderId(String tranOrderId) {
        this.tranOrderId = tranOrderId;
        notifyPropertyChanged(BR.tranOrderId);
    }

    @Bindable
    public String getTranSimpleDesc() {
        return tranSimpleDesc;
    }

    public void setTranSimpleDesc(String tranSimpleDesc) {
        this.tranSimpleDesc = tranSimpleDesc;
        notifyPropertyChanged(BR.tranSimpleDesc);
    }

    @Bindable
    public String getTranDesc() {
        return tranDesc;
    }

    public void setTranDesc(String tranDesc) {
        this.tranDesc = tranDesc;
        notifyPropertyChanged(BR.tranDesc);
    }

    @Bindable
    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
        notifyPropertyChanged(BR.totalMoney);
    }

    @Bindable
    public String getAlipayNotifyUrl() {
        return alipayNotifyUrl;
    }

    public void setAlipayNotifyUrl(String alipayNotifyUrl) {
        this.alipayNotifyUrl = alipayNotifyUrl;
        notifyPropertyChanged(BR.alipayNotifyUrl);
    }

    @Bindable
    public int getCanUseWallet() {
        return canUseWallet;
    }

    public void setCanUseWallet(int canUseWallet) {
        this.canUseWallet = canUseWallet;
        notifyPropertyChanged(BR.canUseWallet);
    }
}
