package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;

import rx.Observable;

public class saveBlackBoxPersonInfoApi extends BaseEntity<Long> {
    int age;//年龄
    String wxNum = "";//微信号
    long provinceId;  //省份ID  存的时候必传
    long cityId;       //城市ID  存的时候必传
    int sex;   //性别      	存的时候必传

    public saveBlackBoxPersonInfoApi setAge(int age) {
        this.age = age;
        return this;
    }

    public saveBlackBoxPersonInfoApi setWxNum(String wxNum) {
        this.wxNum = wxNum;
        return this;
    }

    public saveBlackBoxPersonInfoApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public saveBlackBoxPersonInfoApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public saveBlackBoxPersonInfoApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public saveBlackBoxPersonInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.saveBlackBoxPersonInfo(age, wxNum, 1, provinceId, cityId, sex);
    }
}
