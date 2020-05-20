package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.MineNews;

import java.util.List;

import rx.Observable;

public class dynNewMsgListApi extends BaseEntity<List<MineNews>> {
    int reviewType;//1评论  2.点赞 3.礼物
    int pageNo;

    public dynNewMsgListApi setReviewType(int reviewType) {
        this.reviewType = reviewType;
        return this;
    }

    public dynNewMsgListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public dynNewMsgListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.dynNewMsgList(pageNo, reviewType);
    }
}
