package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.MemberInfo;

import java.util.List;

import rx.Observable;

public class visitorBySeeMeListApi extends BaseEntity<List<MemberInfo>> {
    int pageNo;

    public visitorBySeeMeListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public visitorBySeeMeListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.visitorBySeeMeList(pageNo);
    }
}
