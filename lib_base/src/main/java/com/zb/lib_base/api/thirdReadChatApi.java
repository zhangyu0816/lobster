package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class thirdReadChatApi extends BaseEntity<BaseResultEntity> {
    long otherUserId;
    int msgChannelType;
    long driftBottleId;

    public thirdReadChatApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public thirdReadChatApi setMsgChannelType(int msgChannelType) {
        this.msgChannelType = msgChannelType;
        return this;
    }

    public thirdReadChatApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public thirdReadChatApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        map.put("otherUserId", otherUserId + "");
        map.put("msgChannelType", msgChannelType + "");
        if (driftBottleId > 0)
            map.put("driftBottleId", driftBottleId + "");

        return methods.thirdReadChat(map);
    }
}
