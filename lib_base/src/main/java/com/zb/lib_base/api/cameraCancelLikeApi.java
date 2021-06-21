package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class cameraCancelLikeApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmResourceId;

    public cameraCancelLikeApi setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        return this;
    }

    public cameraCancelLikeApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.cameraCancelLike(cameraFilmResourceId);
    }
}
