package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderTran;

import rx.Observable;

public class payOrderForTranShareApi extends BaseEntity<OrderTran> {
    String orderNumber;

    public payOrderForTranShareApi setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public payOrderForTranShareApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.payOrderForTranShare(orderNumber);
    }
}
