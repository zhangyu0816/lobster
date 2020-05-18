package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BottleInfo;

import rx.Observable;

public class findBottleApi extends BaseEntity<BottleInfo> {

    public findBottleApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("打捞漂流瓶");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findBottle();
    }
}
