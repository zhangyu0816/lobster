package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class joinPairPoolApi extends BaseEntity<BaseResultEntity> {
    String longitude;//经度
    String latitude; //纬度
    long provinceId;
    long cityId;
    long districtId;

    public joinPairPoolApi setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public joinPairPoolApi setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public joinPairPoolApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public joinPairPoolApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public joinPairPoolApi setDistrictId(long districtId) {
        this.districtId = districtId;
        return this;
    }

    public joinPairPoolApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.joinPairPool(longitude, latitude, provinceId, cityId, districtId);
    }
}
