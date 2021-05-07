package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.MemberInfo;

import java.util.List;

import rx.Observable;

public class otherConcernsApi extends BaseEntity<List<MemberInfo>> {
    int pageNo;
    long otherUserId;

    public otherConcernsApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public otherConcernsApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public otherConcernsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.otherConcerns(pageNo, otherUserId);
    }
}
