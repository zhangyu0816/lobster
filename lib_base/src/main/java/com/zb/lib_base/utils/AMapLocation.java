package com.zb.lib_base.utils;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
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
    //声明定位回调监听器
//    public AMapLocationListener mLocationListener;

    public AMapLocation(Context context) {
        //初始化定位
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
    }

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
                        callBack.success(longitude, latitude, provinceName, MineApp.cityName, districtName);
                }
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
        });
        //启动定位
        mLocationClient.startLocation();
    }

    public interface CallBack {
        void success(String longitude, String latitude, String provinceName, String cityName, String districtName);
    }
}
