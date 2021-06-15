package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Film;

import java.util.List;

import rx.Observable;

public class findCameraFilmsForAllApi extends BaseEntity<List<Film>> {
    int authority;
    int washType;
    int pageNo;

    public findCameraFilmsForAllApi setAuthority(int authority) {
        this.authority = authority;
        return this;
    }

    public findCameraFilmsForAllApi setWashType(int washType) {
        this.washType = washType;
        return this;
    }

    public findCameraFilmsForAllApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public findCameraFilmsForAllApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findCameraFilmsForAll(authority, washType, pageNo);
    }
}
