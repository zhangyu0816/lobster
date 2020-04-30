package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class walletPayTranApi extends BaseEntity<BaseResultEntity> {
    String tranOrderId;

    public walletPayTranApi setTranOrderId(String tranOrderId) {
        this.tranOrderId = tranOrderId;
        return this;
    }

    public walletPayTranApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("支付中");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.walletPayTran(tranOrderId);
    }
}
