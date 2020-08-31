package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.DiscoverInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class dynPiazzaListApi extends BaseEntity<List<DiscoverInfo>> {
    int pageNo;
    long cityId;
    int dynType; //动态类型  1推荐(文字) 2小视频

    public dynPiazzaListApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public dynPiazzaListApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public dynPiazzaListApi setDynType(int dynType) {
        this.dynType = dynType;
        return this;
    }

    public dynPiazzaListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        map.put("cityId", cityId + "");
        map.put("pageNo", pageNo + "");
        map.put("dynType", dynType + "");
        if (MineApp.sex != -1)
            map.put("sex", MineApp.sex + "");
        return methods.dynPiazzaList(map);
    }
}
