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
    int msgChannelType;//渠道类型 1.好友聊天 2.漂流瓶
    long driftBottleId;

    public readOverHistoryMsgApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public readOverHistoryMsgApi setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public readOverHistoryMsgApi setMsgChannelType(int msgChannelType) {
        this.msgChannelType = msgChannelType;
        return this;
    }

    public readOverHistoryMsgApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public readOverHistoryMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.readOverHistoryMsg(otherUserId, messageId, msgChannelType, driftBottleId);
    }
}
