package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.PersonInfo;

import rx.Observable;

public class getBlackBoxPersonInfoApi extends BaseEntity<PersonInfo> {
    String number;//单号

    public getBlackBoxPersonInfoApi setNumber(String number) {
        this.number = number;
        return this;
    }

    public getBlackBoxPersonInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getBlackBoxPersonInfo(number);
    }
}
