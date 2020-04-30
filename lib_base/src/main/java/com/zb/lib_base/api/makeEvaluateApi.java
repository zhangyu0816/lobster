package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;

import rx.Observable;

public class makeEvaluateApi extends BaseEntity<Integer> {
    long otherUserId; //对方userId
    int likeOtherStatus; //0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）

    public makeEvaluateApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public makeEvaluateApi setLikeOtherStatus(int likeOtherStatus) {
        this.likeOtherStatus = likeOtherStatus;
        return this;
    }

    public makeEvaluateApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.makeEvaluate(otherUserId, likeOtherStatus);
    }
}
