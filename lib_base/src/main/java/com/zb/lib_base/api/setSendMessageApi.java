package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class setSendMessageApi extends BaseEntity<Integer> {
    private int useType;

    public setSendMessageApi setUseType(int useType) {
        this.useType = useType;
        return this;
    }

    public setSendMessageApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (useType != -1)
            map.put("useType", useType + "");
        return methods.setSendMessage(map);
    }
}
