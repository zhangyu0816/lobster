package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.Review;

import java.util.List;

import rx.Observable;

public class seeReviewsApi extends BaseEntity<List<Review>> {
    long friendDynId;  //动态的Id
    int timeSortType;  //时间排序  0 正序  1 倒序
    int pageNo;
    int row;

    public seeReviewsApi setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
        return this;
    }

    public seeReviewsApi setTimeSortType(int timeSortType) {
        this.timeSortType = timeSortType;
        return this;
    }

    public seeReviewsApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public seeReviewsApi setRow(int row) {
        this.row = row;
        return this;
    }

    public seeReviewsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.seeReviews(friendDynId, timeSortType, pageNo, row);
    }
}
