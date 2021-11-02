package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderTran;

import rx.Observable;

public class payOrderForTranLoveApi extends BaseEntity<OrderTran> {
    String number;

    public payOrderForTranLoveApi setNumber(String number) {
        this.number = number;
        return this;
    }

    public payOrderForTranLoveApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.payOrderForTranLove(number);
    }
}
