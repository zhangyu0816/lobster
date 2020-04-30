package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class VipOrder extends BaseObservable {
    String orderNumber = ""; //订单号
    String orderTitle = "";
    int orderStatus;   // 10 代付款  200 支付成功. TranStatusType
    double productPrice;   //产品单价
    int productCount;  //数量
    double totalMoney; //订单总金额

    @Bindable
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        notifyPropertyChanged(BR.orderNumber);
    }

    @Bindable
    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
        notifyPropertyChanged(BR.orderTitle);
    }

    @Bindable
    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
        notifyPropertyChanged(BR.orderStatus);
    }

    @Bindable
    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
        notifyPropertyChanged(BR.productPrice);
    }

    @Bindable
    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
        notifyPropertyChanged(BR.productCount);
    }

    @Bindable
    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
        notifyPropertyChanged(BR.totalMoney);
    }
}
