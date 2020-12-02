package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.CheckUser;

import rx.Observable;

public class checkUserNameApi extends BaseEntity<CheckUser> {
    String userName;

    public checkUserNameApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public checkUserNameApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.checkUserName(userName);
    }
}
