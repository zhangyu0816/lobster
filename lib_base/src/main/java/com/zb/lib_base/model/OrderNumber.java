package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class OrderNumber extends BaseObservable {
    String orderNumber = ""; //订单号
    String orderTitle = "";
    int orderStatus;   // 10 代付款  200 支付成功. TranStatusType
    double productPrice;   //产品单价
    int productCount;  //数量
    double totalMoney; //订单总金额
    String createTime = "";
    int isPayed;  //是否已支付  0未支付  调用支付  1.已支付-提示下单成功

    String number = "";

    @Bindable
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        notifyPropertyChanged(BR.orderNumber);
    }

    @Bindable public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
        notifyPropertyChanged(BR.orderTitle);
    }

    @Bindable public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
        notifyPropertyChanged(BR.orderStatus);
    }

    @Bindable public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
        notifyPropertyChanged(BR.productPrice);
    }

    @Bindable public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
        notifyPropertyChanged(BR.productCount);
    }

    @Bindable public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
        notifyPropertyChanged(BR.totalMoney);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable public int getIsPayed() {
        return isPayed;
    }

    public void setIsPayed(int isPayed) {
        this.isPayed = isPayed;
        notifyPropertyChanged(BR.isPayed);
    }

    @Bindable public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        notifyPropertyChanged(BR.number);
    }
}
