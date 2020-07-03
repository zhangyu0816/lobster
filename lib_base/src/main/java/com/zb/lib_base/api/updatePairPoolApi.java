package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class updatePairPoolApi extends BaseEntity<BaseResultEntity> {
    String longitude;//经度
    String latitude; //纬度
    long provinceId;
    long cityId;
    long districtId;

    public updatePairPoolApi setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public updatePairPoolApi setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public updatePairPoolApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public updatePairPoolApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public updatePairPoolApi setDistrictId(long districtId) {
        this.districtId = districtId;
        return this;
    }

    public updatePairPoolApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.updatePairPool(longitude, latitude, provinceId, cityId, districtId);
    }
}
