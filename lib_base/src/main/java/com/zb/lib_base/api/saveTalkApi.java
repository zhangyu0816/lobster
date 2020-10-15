package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FlashUser;

import rx.Observable;

public class saveTalkApi extends BaseEntity<FlashUser> {
    long otherUserId;

    public saveTalkApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public saveTalkApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.saveTalk(otherUserId);
    }
}
