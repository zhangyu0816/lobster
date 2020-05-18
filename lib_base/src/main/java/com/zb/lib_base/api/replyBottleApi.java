package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class replyBottleApi extends BaseEntity<BaseResultEntity> {
    long driftBottleId;//漂流瓶id
    String text;          //回复内容

    public replyBottleApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public replyBottleApi setText(String text) {
        this.text = text;
        return this;
    }

    public replyBottleApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("正在回信");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.replyBottle(driftBottleId, text);
    }
}
