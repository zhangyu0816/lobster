package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class humanFaceApi extends BaseEntity<BaseResultEntity> {
    String faceImage;

    public humanFaceApi setFaceImage(String faceImage) {
        this.faceImage = faceImage;
        return this;
    }

    public humanFaceApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交认证图片");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.humanFace(1, faceImage, "");
    }
}
