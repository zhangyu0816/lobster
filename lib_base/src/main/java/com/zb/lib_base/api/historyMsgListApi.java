package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.PrivateMsg;

import java.util.List;

import rx.Observable;

public class historyMsgListApi extends BaseEntity<List<PrivateMsg>> {
    long otherUserId;//  他人userId
    int pageNo;//页数

    public historyMsgListApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public historyMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public historyMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.historyMsgList(otherUserId, pageNo);
    }
}
