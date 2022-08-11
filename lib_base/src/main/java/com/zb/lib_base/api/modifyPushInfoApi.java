package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.utils.PreferenceUtil;

import rx.Observable;

public class modifyPushInfoApi extends BaseEntity<BaseResultEntity> {

    public modifyPushInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.modifyPushInfo(PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"),
                PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"),
                PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"), 2);
    }
}
