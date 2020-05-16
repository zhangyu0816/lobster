package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.OrderNumber;

import rx.Observable;

public class submitOpenedMemberOrderApi extends BaseEntity<OrderNumber> {
    long memberOfOpenedProductId;

    public submitOpenedMemberOrderApi setMemberOfOpenedProductId(long memberOfOpenedProductId) {
        this.memberOfOpenedProductId = memberOfOpenedProductId;
        return this;
    }

    public submitOpenedMemberOrderApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交开通VIP订单");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.submitOpenedMemberOrder(memberOfOpenedProductId, 1);
    }
}
