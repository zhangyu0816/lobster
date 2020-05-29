package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.BaseEntity;

import java.util.List;

import rx.Observable;

public class bankInfoListApi extends BaseEntity<List<BankInfo>> {

    public bankInfoListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.bankInfoList();
    }
}
