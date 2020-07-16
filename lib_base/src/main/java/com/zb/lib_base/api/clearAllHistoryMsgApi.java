package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class clearAllHistoryMsgApi extends BaseEntity<BaseResultEntity> {
    long otherUserId;
    int msgChannelType;//渠道类型 1.好友聊天 2.漂流瓶
    long driftBottleId;

    public clearAllHistoryMsgApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public clearAllHistoryMsgApi setMsgChannelType(int msgChannelType) {
        this.msgChannelType = msgChannelType;
        return this;
    }

    public clearAllHistoryMsgApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public clearAllHistoryMsgApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.clearAllHistoryMsg(otherUserId, msgChannelType, driftBottleId);
    }
}
