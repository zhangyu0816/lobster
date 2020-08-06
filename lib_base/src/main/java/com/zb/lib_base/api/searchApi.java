package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.MemberInfo;

import java.util.List;

import rx.Observable;

public class searchApi extends BaseEntity<List<MemberInfo>> {
    int pageNo;
    String keyWord;
    long cityId;
    int sex;
    int minAge;
    int maxAge;

    public searchApi setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public searchApi setKeyWord(String keyWord) {
        this.keyWord = keyWord;
        return this;
    }

    public searchApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public searchApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public searchApi setMinAge(int minAge) {
        this.minAge = minAge;
        return this;
    }

    public searchApi setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public searchApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.search(pageNo, keyWord, cityId, sex, minAge, maxAge);
    }
}
