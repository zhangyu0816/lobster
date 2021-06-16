package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;

import rx.Observable;

public class deleteUserApi extends BaseEntity<String> {

    public deleteUserApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("注销账号");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.deleteUser();
    }
}
