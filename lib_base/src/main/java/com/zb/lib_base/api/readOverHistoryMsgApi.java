package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

/**
 * Created by DIY on 2019-03-14.
 */

public class readOverHistoryMsgApi extends BaseEntity<BaseResultEntity> {
    long otherUserId;
    long messageId;

    public readOverHistoryMsgApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public readOverHistoryMsgApi setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public readOverHistoryMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.readOverHistoryMsg(otherUserId, messageId);
    }
}
