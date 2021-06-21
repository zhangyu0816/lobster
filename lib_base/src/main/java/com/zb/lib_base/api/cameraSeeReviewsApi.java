package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FilmComment;

import java.util.List;

import rx.Observable;

public class cameraSeeReviewsApi extends BaseEntity<List<FilmComment>> {
    long cameraFilmResourceId; //胶卷资源id
    int pageNo;

    public cameraSeeReviewsApi setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
        return this;
    }

    public cameraSeeReviewsApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public cameraSeeReviewsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.cameraSeeReviews(cameraFilmResourceId, 1, pageNo, 10);
    }
}
