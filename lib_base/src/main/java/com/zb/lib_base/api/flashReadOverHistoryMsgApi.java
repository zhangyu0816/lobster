package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class flashReadOverHistoryMsgApi extends BaseEntity<BaseResultEntity> {
    long otherUserId;// 他人userId
    long messageId;// 最大消息id
    long flashTalkId;    //闪聊id

    public flashReadOverHistoryMsgApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public flashReadOverHistoryMsgApi setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public flashReadOverHistoryMsgApi setFlashTalkId(long flashTalkId) {
        this.flashTalkId = flashTalkId;
        return this;
    }

    public flashReadOverHistoryMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.flashReadOverHistoryMsg(otherUserId, flashTalkId, messageId);
    }
}
