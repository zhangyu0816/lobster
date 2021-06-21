package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class cameraDoLikeApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmResourceId;

    public cameraDoLikeApi setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        return this;
    }

    public cameraDoLikeApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.cameraDoLike(cameraFilmResourceId);
    }
}
