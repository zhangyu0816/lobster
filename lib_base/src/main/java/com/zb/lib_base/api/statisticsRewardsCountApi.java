package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.PersonInfo;

import rx.Observable;

public class statisticsRewardsCountApi extends BaseEntity<PersonInfo> {
    int tranStatusType;  //订单状态  10.代付款 (尚未派发奖励)    200.交易成功

    public statisticsRewardsCountApi setTranStatusType(int tranStatusType) {
        this.tranStatusType = tranStatusType;
        return this;
    }

    public statisticsRewardsCountApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.statisticsRewardsCount(tranStatusType);
    }
}
