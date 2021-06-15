package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FilmInfo;

import java.util.List;

import rx.Observable;

public class findCameraFilmsResourceListApi extends BaseEntity<List<FilmInfo>> {
    long cameraFilmId;

    public findCameraFilmsResourceListApi setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        return this;
    }

    public findCameraFilmsResourceListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("胶卷详情...");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findCameraFilmsResourceList(cameraFilmId);
    }
}
