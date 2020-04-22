package com.zb.lib_base.model;

import io.realm.RealmObject;

public class ProvinceInfo extends RealmObject {

    private String provinceName = "";
    private long provinceId = 0;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }


}
