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

    public pickBottleApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public pickBottleApi setDriftBottleType(int driftBottleType) {
        this.driftBottleType = driftBottleType;
        return this;
    }

    public pickBottleApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.pickBottle(driftBottleId, driftBottleType);
    }
}
