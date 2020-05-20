package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.TranRecord;

import rx.Observable;

public class tranSingleRecordApi extends BaseEntity<TranRecord> {
    String tranOrderId;

    public tranSingleRecordApi setTranOrderId(String tranOrderId) {
        this.tranOrderId = tranOrderId;
        return this;
    }

    public tranSingleRecordApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("交易详情");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.tranSingleRecord(tranOrderId);
    }
}
