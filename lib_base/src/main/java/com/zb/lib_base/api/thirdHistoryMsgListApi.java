package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.HistoryMsg;

import java.util.List;

import rx.Observable;

public class thirdHistoryMsgListApi extends BaseEntity<List<HistoryMsg>> {
    long otherUserId;//  他人userId
    int pageNo;//页数

    public thirdHistoryMsgListApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public thirdHistoryMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public thirdHistoryMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.thirdHistoryMsgList(otherUserId, pageNo, 2);
    }
}
