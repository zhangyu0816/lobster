package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class removeBankCardApi extends BaseEntity<BaseResultEntity> {
    long bankAccountId;

    public removeBankCardApi setBankAccountId(long bankAccountId) {
        this.bankAccountId = bankAccountId;
        return this;
    }

    public removeBankCardApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.removeBankCard(bankAccountId);
    }
}
