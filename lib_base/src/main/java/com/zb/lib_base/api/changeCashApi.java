package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.TranRecord;

import rx.Observable;

public class changeCashApi extends BaseEntity<TranRecord> {
    String money;
    long bankAccountId;

    public changeCashApi setMoney(String money) {
        this.money = money;
        return this;
    }

    public changeCashApi setBankAccountId(long bankAccountId) {
        this.bankAccountId = bankAccountId;
        return this;
    }

    public changeCashApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交提现信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.changeCash(money,bankAccountId);
    }
}
