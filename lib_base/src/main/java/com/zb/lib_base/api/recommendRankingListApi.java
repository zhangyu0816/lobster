package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.RecommendInfo;

import java.util.List;

import rx.Observable;

public class recommendRankingListApi extends BaseEntity<List<RecommendInfo>> {
    long cityId;
    int sex;

    public recommendRankingListApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public recommendRankingListApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public recommendRankingListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.recommendRankingList(cityId, sex);
    }
}
