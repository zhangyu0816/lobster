package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class readNewDynMsgAllApi extends BaseEntity<BaseResultEntity> {
    int reviewType;

    public readNewDynMsgAllApi setReviewType(int reviewType) {
        this.reviewType = reviewType;
        return this;
    }

    public readNewDynMsgAllApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (reviewType > 0)
            map.put("reviewType", reviewType + "");
        return methods.readNewDynMsgAll(map);
    }
}
