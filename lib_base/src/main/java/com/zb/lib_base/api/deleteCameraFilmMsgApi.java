package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class deleteCameraFilmMsgApi extends BaseEntity<BaseResultEntity> {
    long cameraFilmResourceReviewMsgId;

    public deleteCameraFilmMsgApi setCameraFilmResourceReviewMsgId(long cameraFilmResourceReviewMsgId) {
        this.cameraFilmResourceReviewMsgId = cameraFilmResourceReviewMsgId;
        return this;
    }

    public deleteCameraFilmMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.deleteCameraFilmMsg(cameraFilmResourceReviewMsgId);
    }
}
