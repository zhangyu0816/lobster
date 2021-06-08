package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class saveCameraFilmResourceApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmId;
    String image;

    public saveCameraFilmResourceApi setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        return this;
    }

    public saveCameraFilmResourceApi setImage(String image) {
        this.image = image;
        return this;
    }

    public saveCameraFilmResourceApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.saveCameraFilmResource(cameraFilmId, image);
    }
}
