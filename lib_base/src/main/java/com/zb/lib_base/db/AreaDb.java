package com.zb.lib_base.db;

import com.zb.lib_base.model.CityInfo;
import com.zb.lib_base.model.DistrictInfo;
import com.zb.lib_base.model.ProvinceInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class AreaDb extends BaseDao {
    public volatile static AreaDb INSTANCE;

    public AreaDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static AreaDb getInstance() {
        if (INSTANCE == null) {
            synchronized (AreaDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AreaDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
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

    // 获取省份名称
    public long getProvinceId(String provinceName) {
        beginTransaction();
        ProvinceInfo provinceInfo = realm.where(ProvinceInfo.class).equalTo("provinceName", provinceName).findFirst();
        commitTransaction();
        return provinceInfo == null ? 0 : provinceInfo.getProvinceId();
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

    // 获取城市id
    public long getCityId(String cityName) {
        beginTransaction();
        CityInfo cityInfo = realm.where(CityInfo.class).equalTo("cityName", cityName).findFirst();
        commitTransaction();
        return cityInfo == null ? 0 : cityInfo.getCityId();
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

    // 获取地区名称
    public long getDistrictId(String districtName) {
        beginTransaction();
        DistrictInfo districtInfo = realm.where(DistrictInfo.class).equalTo("districtName", districtName).findFirst();
        commitTransaction();
        return districtInfo == null ? 0 : districtInfo.getDistrictId();
    }
}

