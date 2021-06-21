package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FilmInfo;

import rx.Observable;

public class findCameraFilmsResourceApi extends BaseEntity<FilmInfo> {
    long cameraFilmResourceId;

    public findCameraFilmsResourceApi setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        return this;
    }

    public findCameraFilmsResourceApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("获取胶卷资源详情...");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findCameraFilmsResource(cameraFilmResourceId);
    }
}
