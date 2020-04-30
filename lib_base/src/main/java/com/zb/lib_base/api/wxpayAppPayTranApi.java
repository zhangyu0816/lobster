package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.WXPay;

import rx.Observable;

public class wxpayAppPayTranApi extends BaseEntity<WXPay> {
    String tranOrderId;

    public wxpayAppPayTranApi setTranOrderId(String tranOrderId) {
        this.tranOrderId = tranOrderId;
        return this;
    }

    public wxpayAppPayTranApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("调起微信支付");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.wxpayAppPayTran(tranOrderId);
    }
}
