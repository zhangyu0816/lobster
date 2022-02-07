package com.zb.lib_base.utils;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.services.core.ServiceSettings;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;

/**
 * Created by DIY on 2018-06-27.
 */

public class AMapLocation {

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption;

    public AMapLocation(Context context) {
        ServiceSettings.updatePrivacyShow(context, true, true);
        ServiceSettings.updatePrivacyAgree(context, true);
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(context);
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否允许模拟位置,默认为true，允许模拟位置
            mLocationOption.setMockEnable(true);
            //关闭缓存机制
            mLocationOption.setLocationCacheEnable(false);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    latitude=0.0#longitude=0.0#province=#coordType=GCJ02#city=#district=#cityCode=#adCode=#address=#country=#road=#poiName=#street=#streetNum=#aoiName=#poiid=#floor=#errorCode=12#errorInfo=缺少定位权限#locationDetail=定位服务没有开启，请在设置中打开定位服务开关#1206#description=#locationType=0#conScenario=0
    public void start(RxAppCompatActivity activity, CallBack callBack) {
        //设置定位回调监听
        mLocationClient.setLocationListener(location -> {
            if (location != null) {
                if (location.getErrorCode() == 0) {
                    MineApp.cityName = location.getCity();
                    String provinceName = location.getProvince();
                    String districtName = location.getDistrict();
                    String address = location.getAddress();
                    String longitude = location.getLongitude() + "";
                    String latitude = location.getLatitude() + "";
                    PreferenceUtil.saveStringValue(activity, "longitude", longitude);
                    PreferenceUtil.saveStringValue(activity, "latitude", latitude);
                    PreferenceUtil.saveStringValue(activity, "provinceName", provinceName);
                    PreferenceUtil.saveStringValue(activity, "cityName", MineApp.cityName);
                    PreferenceUtil.saveStringValue(activity, "districtName", districtName);
                    PreferenceUtil.saveStringValue(activity, "address", address);
                    if (callBack != null)
                        callBack.success();
                } else {
                    if (PreferenceUtil.readStringValue(activity, "longitude").isEmpty()) {
                        SCToastUtil.showToast(activity, "定位失败，请检查定位是否开启或连接WIFI重新尝试", true);
                    } else {
                        if (callBack != null)
                            callBack.success();
                    }
                }
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
        });
        //启动定位
        mLocationClient.startLocation();
    }

    public interface CallBack {
        void success();
    }
}
