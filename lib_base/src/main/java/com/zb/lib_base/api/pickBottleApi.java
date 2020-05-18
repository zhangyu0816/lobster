package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class pickBottleApi extends BaseEntity<BaseResultEntity> {
    long driftBottleId;//漂流瓶id
    int driftBottleType;//漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
    long otherUserId;//拾起人id    [可空 状态传2时 必填]

    public pickBottleApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public pickBottleApi setDriftBottleType(int driftBottleType) {
        this.driftBottleType = driftBottleType;
        return this;
    }

    public pickBottleApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public pickBottleApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.pickBottle(driftBottleId, driftBottleType, otherUserId);
    }
}
