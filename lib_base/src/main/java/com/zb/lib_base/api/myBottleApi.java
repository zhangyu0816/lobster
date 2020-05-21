package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BottleInfo;

import rx.Observable;

public class myBottleApi extends BaseEntity<BottleInfo> {
    long driftBottleId;

    public myBottleApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public myBottleApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("加载漂流瓶信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.myBottle(driftBottleId);
    }
}
