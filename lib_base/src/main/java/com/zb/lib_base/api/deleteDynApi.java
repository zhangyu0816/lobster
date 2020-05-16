package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class deleteDynApi extends BaseEntity<BaseResultEntity> {
    long friendDynId;

    public deleteDynApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public deleteDynApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("删除动态");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.deleteDyn(friendDynId);
    }
}
