package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.LoveMoney;

import java.util.List;

import rx.Observable;

public class rewardsOrderListApi extends BaseEntity<List<LoveMoney>> {
    int pageNo;

    public rewardsOrderListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public rewardsOrderListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("加载收益数据");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.rewardsOrderList(200, pageNo, 10);
    }
}
