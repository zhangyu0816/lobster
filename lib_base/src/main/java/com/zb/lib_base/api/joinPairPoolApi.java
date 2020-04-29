package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class joinPairPoolApi extends BaseEntity<BaseResultEntity> {
    String longitude;//经度
    String latitude; //纬度

    public joinPairPoolApi setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public joinPairPoolApi setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public joinPairPoolApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.joinPairPool(longitude, latitude);
    }
}
