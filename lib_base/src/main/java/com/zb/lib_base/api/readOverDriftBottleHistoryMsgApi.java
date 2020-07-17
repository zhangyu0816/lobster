package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class readOverDriftBottleHistoryMsgApi extends BaseEntity<BaseResultEntity> {
    long otherUserId;
    long driftBottleId;
    long messageId;

    public readOverDriftBottleHistoryMsgApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public readOverDriftBottleHistoryMsgApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public readOverDriftBottleHistoryMsgApi setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public readOverDriftBottleHistoryMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.readOverDriftBottleHistoryMsg(otherUserId, driftBottleId, messageId);
    }
}
