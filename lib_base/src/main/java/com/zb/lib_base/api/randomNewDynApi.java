package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.DiscoverInfo;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class randomNewDynApi extends BaseEntity<DiscoverInfo> {

    public randomNewDynApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (MineApp.sex != -1)
            map.put("sex", MineApp.sex + "");
        return methods.randomNewDyn(map);
    }
}
