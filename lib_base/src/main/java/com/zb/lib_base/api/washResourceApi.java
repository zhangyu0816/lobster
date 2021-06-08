package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class washResourceApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmId;

    public washResourceApi setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        return this;
    }

    public washResourceApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("正在冲洗...");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.washResource(cameraFilmId);
    }
}
