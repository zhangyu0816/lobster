package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.ImAccount;

import rx.Observable;

/**
 * Created by DIY on 2019-03-13.
 */

public class otherImAccountInfoApi extends BaseEntity<ImAccount> {

    long otherUserId;

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public otherImAccountInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.otherImAccountInfo(otherUserId, 2);
    }
}
