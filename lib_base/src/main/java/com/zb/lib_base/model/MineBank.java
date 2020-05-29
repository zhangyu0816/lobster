package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MineBank extends BaseObservable {
    long id;
    long userId;
    String bankName = ""; //银行名
    String bankLogo = ""; //银行Logo
    int bankType;//是1.银行  2交易平台
    String accountNo = "";    //银行卡号 Or 支付宝?..百付宝?..
    String accountPerson = "";   //账号所有人的名字
    String openAccountLocation = ""; //开户网点

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
        notifyPropertyChanged(BR.bankName);
    }

    @Bindable public String getBankLogo() {
        return bankLogo;
    }

    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
        notifyPropertyChanged(BR.bankLogo);
    }

    @Bindable public int getBankType() {
        return bankType;
    }

    public void setBankType(int bankType) {
        this.bankType = bankType;
        notifyPropertyChanged(BR.bankType);
    }

    @Bindable public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
        notifyPropertyChanged(BR.accountNo);
    }

    @Bindable public String getAccountPerson() {
        return accountPerson;
    }

    public void setAccountPerson(String accountPerson) {
        this.accountPerson = accountPerson;
        notifyPropertyChanged(BR.accountPerson);
    }

    @Bindable public String getOpenAccountLocation() {
        return openAccountLocation;
    }

    public void setOpenAccountLocation(String openAccountLocation) {
        this.openAccountLocation = openAccountLocation;
        notifyPropertyChanged(BR.openAccountLocation);
    }
}
