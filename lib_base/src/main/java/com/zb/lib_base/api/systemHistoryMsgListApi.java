package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.SystemMsg;

import java.util.List;

import rx.Observable;

public class systemHistoryMsgListApi extends BaseEntity<List<SystemMsg>> {
    int pageNo;

    public systemHistoryMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public systemHistoryMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.systemHistoryMsgList(pageNo);
    }
}
