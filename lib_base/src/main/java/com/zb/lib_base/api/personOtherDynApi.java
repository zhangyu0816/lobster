package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.DiscoverInfo;

import java.util.List;

import rx.Observable;

public class personOtherDynApi extends BaseEntity<List<DiscoverInfo>> {
    long otherUserId;
    int pageNo;
    int dynType;

    public personOtherDynApi setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }

    public personOtherDynApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public personOtherDynApi setDynType(int dynType) {
        this.dynType = dynType;
        return this;
    }

    public personOtherDynApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("加载动态信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.personOtherDyn(otherUserId, pageNo, 1, dynType);
    }
}
