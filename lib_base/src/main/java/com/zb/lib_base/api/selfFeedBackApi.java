package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.FeedbackInfo;

import java.util.List;

import rx.Observable;

public class selfFeedBackApi extends BaseEntity<List<FeedbackInfo>> {
    int pageNumber;

    public selfFeedBackApi setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public selfFeedBackApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.selfFeedBack(pageNumber);
    }
}
