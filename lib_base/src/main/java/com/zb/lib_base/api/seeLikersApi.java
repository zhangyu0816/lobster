package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Review;

import java.util.List;

import rx.Observable;

public class seeLikersApi extends BaseEntity<List<Review>> {
    long friendDynId;
    int pageNo;

    public seeLikersApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public seeLikersApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public seeLikersApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.seeLikers(friendDynId, pageNo, 20);
    }
}
