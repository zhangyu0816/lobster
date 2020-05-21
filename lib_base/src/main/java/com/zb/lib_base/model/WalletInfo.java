package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class WalletInfo extends BaseObservable {
    private long userId;
    private double wallet = 0;// 钱
    private double frozenWallet = 0;// 待确认金额(冻结)
    private int popularity = 0; // 人气(浏览量)
    private double canWithdrawCreditWallet = 0; // 可提现金额
    private int likeMeNum;   //喜欢我的人

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
        notifyPropertyChanged(BR.wallet);
    }

    @Bindable public double getFrozenWallet() {
        return frozenWallet;
    }

    public void setFrozenWallet(double frozenWallet) {
        this.frozenWallet = frozenWallet;
        notifyPropertyChanged(BR.frozenWallet);
    }

    @Bindable public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
        notifyPropertyChanged(BR.popularity);
    }

    @Bindable public double getCanWithdrawCreditWallet() {
        return canWithdrawCreditWallet;
    }

    public void setCanWithdrawCreditWallet(double canWithdrawCreditWallet) {
        this.canWithdrawCreditWallet = canWithdrawCreditWallet;
        notifyPropertyChanged(BR.canWithdrawCreditWallet);
    }

    @Bindable public int getLikeMeNum() {
        return likeMeNum;
    }

    public void setLikeMeNum(int likeMeNum) {
        this.likeMeNum = likeMeNum;
        notifyPropertyChanged(BR.likeMeNum);
    }
}
