package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.TranRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class tranRecordsApi extends BaseEntity<List<TranRecord>> {
    int pageNo;
    int tranType;

    public tranRecordsApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public tranRecordsApi setTranType(int tranType) {
        this.tranType = tranType;
        return this;
    }

    public tranRecordsApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        map.put("pageNo", pageNo + "");
        if (tranType > 0)
            map.put("tranType", tranType + "");
        map.put("row", "10");

        return methods.tranRecords(map);
    }
}
