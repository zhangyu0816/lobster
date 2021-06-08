package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Film;

import java.util.List;

import rx.Observable;

public class findCameraFilmsApi extends BaseEntity<List<Film>> {
    int isEnable; //是否可用   1.可用  0.不可用

    public findCameraFilmsApi setIsEnable(int isEnable) {
        this.isEnable = isEnable;
        return this;
    }

    public findCameraFilmsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findCameraFilms(isEnable);
    }
}
