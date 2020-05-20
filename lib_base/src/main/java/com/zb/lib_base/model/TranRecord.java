package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class TranRecord extends BaseObservable {
    long userId;   //处理对象所属userId  //信息所属--该条记录只能这个用户能查看(自己,不显示)
    long tranUserId; //对方的userId
    String image;  //对方的头像
    String tranOrderId;  //交易订单号 (生成必须唯一)
    double tranMoney;     //金额
    String useWay;     //交易事件描述
    int useType;      //类型   1 账户加钱(+) 2：账户扣钱(-)
    String createTime;  //创建时间
    int statusType;      //交易状态
    double realMoney;         //实收金额
    double feeMoney;         //手续费
    float feeRates;          //手续费率  0.01 = 1%  //(Max)1
    String nick; //昵称  
    String paySayText; //说的话
    int interfaceType;    //使用哪个支付接口   1.系统充值  2.钱包  3.支付宝快捷支付接口  4.微信APP支付
    int tranType;    //交易分类  1.充值  2.提现 3.订单服务费
    //   1,"充值 
//           2,"提现 
//           3,"订单平台服务费  
//           4,"个人信息首页置顶  
//           5,"下级消费分成  
//           6,"技能服务订单付款  
//           7,"开通会员  
//           8,"评论付款  
//           9,"红包付款  
//           10,"私密相册  
//           11,"相册打赏  
//           12,"好友红包
    String userOrderNumber;// 订单号 -用户的预约订单

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public long getTranUserId() {
        return tranUserId;
    }

    public void setTranUserId(long tranUserId) {
        this.tranUserId = tranUserId;
        notifyPropertyChanged(BR.tranUserId);
    }

    @Bindable public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable public String getTranOrderId() {
        return tranOrderId;
    }

    public void setTranOrderId(String tranOrderId) {
        this.tranOrderId = tranOrderId;
        notifyPropertyChanged(BR.tranOrderId);
    }

    @Bindable public double getTranMoney() {
        return tranMoney;
    }

    public void setTranMoney(double tranMoney) {
        this.tranMoney = tranMoney;
        notifyPropertyChanged(BR.tranMoney);
    }

    @Bindable public String getUseWay() {
        return useWay;
    }

    public void setUseWay(String useWay) {
        this.useWay = useWay;
        notifyPropertyChanged(BR.useWay);
    }

    @Bindable public int getUseType() {
        return useType;
    }

    public void setUseType(int useType) {
        this.useType = useType;
        notifyPropertyChanged(BR.useType);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable public int getStatusType() {
        return statusType;
    }

    public void setStatusType(int statusType) {
        this.statusType = statusType;
        notifyPropertyChanged(BR.statusType);
    }

    @Bindable public double getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(double realMoney) {
        this.realMoney = realMoney;
        notifyPropertyChanged(BR.realMoney);
    }

    @Bindable public double getFeeMoney() {
        return feeMoney;
    }

    public void setFeeMoney(double feeMoney) {
        this.feeMoney = feeMoney;
        notifyPropertyChanged(BR.feeMoney);
    }

    @Bindable public float getFeeRates() {
        return feeRates;
    }

    public void setFeeRates(float feeRates) {
        this.feeRates = feeRates;
        notifyPropertyChanged(BR.feeRates);
    }

    @Bindable public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable public String getPaySayText() {
        return paySayText;
    }

    public void setPaySayText(String paySayText) {
        this.paySayText = paySayText;
        notifyPropertyChanged(BR.paySayText);
    }

    @Bindable public int getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
        notifyPropertyChanged(BR.interfaceType);
    }

    @Bindable public int getTranType() {
        return tranType;
    }

    public void setTranType(int tranType) {
        this.tranType = tranType;
        notifyPropertyChanged(BR.tranType);
    }

    @Bindable public String getUserOrderNumber() {
        return userOrderNumber;
    }

    public void setUserOrderNumber(String userOrderNumber) {
        this.userOrderNumber = userOrderNumber;
        notifyPropertyChanged(BR.userOrderNumber);
    }
}
