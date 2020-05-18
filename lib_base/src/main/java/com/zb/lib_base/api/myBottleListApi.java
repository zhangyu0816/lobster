package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BottleInfo;

import java.util.List;

import rx.Observable;

public class myBottleListApi extends BaseEntity<List<BottleInfo>> {
    int pageNo;

    public myBottleListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public myBottleListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("加载我的漂流瓶列表");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.myBottleList(pageNo);
    }
}
