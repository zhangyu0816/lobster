package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.LikeMe;

import java.util.List;

import rx.Observable;

public class pairListApi extends BaseEntity<List<LikeMe>> {
    int pageNo;

    public pairListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public pairListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.pairList(pageNo);
    }
}
