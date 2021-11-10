package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class LoveMoney extends BaseObservable {
    long rewardTransactionInfoId;//零钱奖励订单
    double realMoney;//收款
    String createTime = "";//创建时间
    int tranStatusType;  //订单状态  10.代付款 (尚未派发奖励)    200.交易成功

    @Bindable
    public long getRewardTransactionInfoId() {
        return rewardTransactionInfoId;
    }

    public void setRewardTransactionInfoId(long rewardTransactionInfoId) {
        this.rewardTransactionInfoId = rewardTransactionInfoId;
        notifyPropertyChanged(BR.rewardTransactionInfoId);
    }

    @Bindable
    public double getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(double realMoney) {
        this.realMoney = realMoney;
        notifyPropertyChanged(BR.realMoney);
    }

    @Bindable
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable
    public int getTranStatusType() {
        return tranStatusType;
    }

    public void setTranStatusType(int tranStatusType) {
        this.tranStatusType = tranStatusType;
        notifyPropertyChanged(BR.tranStatusType);
    }
}
