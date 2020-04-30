package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.AliPay;
import com.zb.lib_base.model.BaseEntity;

import rx.Observable;

public class alipayFastPayTranApi extends BaseEntity<AliPay> {
    String tranOrderId;

    public alipayFastPayTranApi setTranOrderId(String tranOrderId) {
        this.tranOrderId = tranOrderId;
        return this;
    }

    public alipayFastPayTranApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("调起支付宝支付");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.alipayFastPayTran(tranOrderId);
    }
}
