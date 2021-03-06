package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CityInfo extends RealmObject {
    private long provinceId = 0;
    @PrimaryKey
    private long cityId = 0;
    private String cityName = "";

    public CityInfo() {
    }

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
