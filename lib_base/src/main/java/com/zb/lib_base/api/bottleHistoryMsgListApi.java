package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.HistoryMsg;

import java.util.List;

import rx.Observable;

public class bottleHistoryMsgListApi extends BaseEntity<List<HistoryMsg>> {
    long otherUserId;
    long driftBottleId;
    int pageNo;

    public bottleHistoryMsgListApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public bottleHistoryMsgListApi setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        return this;
    }

    public bottleHistoryMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public bottleHistoryMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.bottleHistoryMsgList(otherUserId, driftBottleId, pageNo);
    }
}
