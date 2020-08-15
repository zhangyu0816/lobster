package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.PairInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class prePairListApi extends BaseEntity<List<PairInfo>> {
    int sex;//性别
    int minAge;//最小年龄
    int maxAge;//最大年龄

    public prePairListApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public prePairListApi setMinAge(int minAge) {
        this.minAge = minAge;
        return this;
    }

    public prePairListApi setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public prePairListApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        if (sex != -1) {
            map.put("sex", sex + "");
        }
        map.put("minAge", minAge + "");
        map.put("maxAge", maxAge + "");
        return methods.prePairList(map);
    }
}
