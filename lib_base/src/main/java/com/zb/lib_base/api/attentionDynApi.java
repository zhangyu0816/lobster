package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.DiscoverInfo;

import java.util.List;

import rx.Observable;

public class attentionDynApi extends BaseEntity<List<DiscoverInfo>> {
    int pageNo;
    int timeSortType;//时间 正序 0   时间 倒序 1   默认

    public attentionDynApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public attentionDynApi setTimeSortType(int timeSortType) {
        this.timeSortType = timeSortType;
        return this;
    }

    public attentionDynApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("加载动态信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.attentionDyn(pageNo, timeSortType);
    }
}
