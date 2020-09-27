package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderNumber;

import rx.Observable;

public class openMakePartnerApi extends BaseEntity<OrderNumber> {
    long markeProductId;

    public openMakePartnerApi setMarkeProductId(long markeProductId) {
        this.markeProductId = markeProductId;
        return this;
    }

    public openMakePartnerApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.openMakePartner(markeProductId);
    }
}
