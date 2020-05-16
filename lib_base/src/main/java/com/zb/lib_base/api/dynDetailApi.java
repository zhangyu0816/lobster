package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.DiscoverInfo;

import rx.Observable;

public class dynDetailApi extends BaseEntity<DiscoverInfo> {
    long friendDynId;

    public dynDetailApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public dynDetailApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("获取动态详情");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.dynDetail(friendDynId);
    }
}
