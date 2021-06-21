package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FilmComment;

import java.util.List;

import rx.Observable;

public class cameraSeeLikersApi extends BaseEntity<List<FilmComment>> {
    long cameraFilmResourceId; //胶卷资源id
    int pageNo;

    public cameraSeeLikersApi setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        return this;
    }

    public cameraSeeLikersApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public cameraSeeLikersApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.cameraSeeLikers(cameraFilmResourceId, pageNo);
    }
}
