package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class flashClearHistoryMsgApi extends BaseEntity<BaseResultEntity> {
    long otherUserId;//  他人userId
    long flashTalkId;//闪聊id

    public flashClearHistoryMsgApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public flashClearHistoryMsgApi setFlashTalkId(long flashTalkId) {
        this.flashTalkId = flashTalkId;
        return this;
    }

    public flashClearHistoryMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.flashClearHistoryMsg(otherUserId, flashTalkId);
    }
}
