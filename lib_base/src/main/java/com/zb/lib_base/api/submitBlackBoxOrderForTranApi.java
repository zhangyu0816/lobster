package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.LoveNumber;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class submitBlackBoxOrderForTranApi extends BaseEntity<LoveNumber> {
    int orderCategory;// 7.盲盒存    8.盲盒取
    long blackBoxPersonInfoId;//盲盒人信息ID   存的话需要传入
    int sex;   //性别

    public submitBlackBoxOrderForTranApi setOrderCategory(int orderCategory) {
        this.orderCategory = orderCategory;
        return this;
    }

    public submitBlackBoxOrderForTranApi setBlackBoxPersonInfoId(long blackBoxPersonInfoId) {
        this.blackBoxPersonInfoId = blackBoxPersonInfoId;
        return this;
    }

    public submitBlackBoxOrderForTranApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public submitBlackBoxOrderForTranApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderCategory", orderCategory);
        if (orderCategory == 7)
            map.put("blackBoxPersonInfoId", blackBoxPersonInfoId);
        else
            map.put("sex", sex);
        return methods.submitBlackBoxOrderForTran(map);
    }
}
