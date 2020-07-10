package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BottleCache;

import java.util.List;

import rx.Observable;

public class driftBottleChatListApi extends BaseEntity<List<BottleCache>> {
    int pageNo;    //页码

    public driftBottleChatListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public driftBottleChatListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.driftBottleChatList(1, pageNo);
    }
}
