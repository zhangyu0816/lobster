package com.zb.lib_base.db;

import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.ProvinceInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class AreaDb extends BaseDao {

    public AreaDb(Realm realm) {
        super(realm);
    }

    // 保存省份信息
    public void saveProvince(ProvinceInfo provinceInfo) {
        beginTransaction();
        realm.insertOrUpdate(provinceInfo);
        commitTransaction();
    }

    // 是否有城市信息
    public boolean hasProvince() {
        beginTransaction();
        RealmResults<ProvinceInfo> results = realm.where(ProvinceInfo.class).findAll();
        commitTransaction();
        return results.size() > 0;
    }

    // 获取省份名称
    public String getProvinceName(long provinceId) {
        beginTransaction();
        ProvinceInfo provinceInfo = realm.where(ProvinceInfo.class).equalTo("provinceId", provinceId).findFirst();
        commitTransaction();
        return provinceInfo == null ? "" : provinceInfo.getProvinceName();
    }

    // 保存城市信息
    public void saveCity(CityInfo cityInfo) {
        beginTransaction();
        realm.insertOrUpdate(cityInfo);
        commitTransaction();
    }

    // 获取省份内的城市列表
    public List<CityInfo> getCityList(long provinceId) {
        beginTransaction();
        List<CityInfo> cityInfoList = new ArrayList<>();
        RealmResults<CityInfo> results = realm.where(CityInfo.class).equalTo("provinceId", provinceId).findAll();
        if (results.size() > 0) {
            cityInfoList.addAll(results);
        }
        commitTransaction();
        return cityInfoList;
    }

    // 获取城市名称
    public String getCityName(long cityId) {
        beginTransaction();
        CityInfo cityInfo = realm.where(CityInfo.class).equalTo("cityId", cityId).findFirst();
        commitTransaction();
        return cityInfo == null ? "" : cityInfo.getCityName();
    }

    // 保存地区信息
    public void saveDistrictInfo(DistrictInfo districtInfo) {
        beginTransaction();
        realm.insertOrUpdate(districtInfo);
        commitTransaction();
    }

    // 获取城市内地区列表
    public List<DistrictInfo> getDistrictList(long cityId) {
        beginTransaction();
        List<DistrictInfo> districtInfoList = new ArrayList<>();
        RealmResults<DistrictInfo> results = realm.where(DistrictInfo.class).equalTo("cityId", cityId).findAll();
        if (results.size() > 0) {
            districtInfoList.addAll(results);
        }
        commitTransaction();
        return districtInfoList;
    }

    // 获取地区名称
    public String getDistrictName(long districtId) {
        beginTransaction();
        DistrictInfo districtInfo = realm.where(DistrictInfo.class).equalTo("districtId", districtId).findFirst();
        commitTransaction();
        return districtInfo == null ? "" : districtInfo.getDistrictName();
    }
}

