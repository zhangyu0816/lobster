package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class shareChangeCashApi extends BaseEntity<BaseResultEntity> {
    double money;
    long bankAccountId;

    public shareChangeCashApi setMoney(double money) {
        this.money = money;
        return this;
    }

    public shareChangeCashApi setBankAccountId(long bankAccountId) {
        this.bankAccountId = bankAccountId;
        return this;
    }

    public shareChangeCashApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.shareChangeCash(money, bankAccountId);
    }
}
