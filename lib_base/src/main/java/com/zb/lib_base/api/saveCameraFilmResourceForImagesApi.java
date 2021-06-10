package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class saveCameraFilmResourceForImagesApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmId;
    String images;

    public saveCameraFilmResourceForImagesApi setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        return this;
    }

    public saveCameraFilmResourceForImagesApi setImages(String images) {
        this.images = images;
        return this;
    }

    public saveCameraFilmResourceForImagesApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.saveCameraFilmResourceForImages(cameraFilmId, images);
    }
}
