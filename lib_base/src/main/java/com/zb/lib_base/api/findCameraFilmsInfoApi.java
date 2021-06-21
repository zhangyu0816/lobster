package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Film;

import rx.Observable;

public class findCameraFilmsInfoApi extends BaseEntity<Film> {
    long cameraFilmId;

    public findCameraFilmsInfoApi setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        return this;
    }

    public findCameraFilmsInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findCameraFilmsInfo(cameraFilmId);
    }
}
