package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class readNewDynMsgAllApi extends BaseEntity<BaseResultEntity> {

    public readNewDynMsgAllApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("清除消息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.readNewDynMsgAll();
    }
}
