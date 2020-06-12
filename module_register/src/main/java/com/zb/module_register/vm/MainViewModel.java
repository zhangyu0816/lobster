package com.zb.module_register.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.MainVMInterface;

import androidx.databinding.ViewDataBinding;

public class MainViewModel extends BaseViewModel implements MainVMInterface {

    private AMapLocation aMapLocation;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        aMapLocation = new AMapLocation(activity);
        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        if (MineApp.cityName.isEmpty()) {
            getPermissions();
        }
    }

    @Override
    public void selectSex(int sex) {
        MineApp.registerInfo.setSex(sex);
        ActivityUtils.getRegisterNick();
        activity.finish();
    }

    @Override
    public void toLogin(View view) {
        ActivityUtils.getRegisterPhone(true);
        activity.finish();
    }

    @Override
    public void toRule(View view) {
        ActivityUtils.getMineWeb("隐私服务条款", HttpManager.BASE_URL + "mobile/yuenar_reg_protocol.html");
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问定位权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setLocation();
                        }

                        @Override
                        public void noPermission() {
                            baseLocation();
                        }
                    }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE);
        } else {
            setLocation();
        }
    }

    private void setLocation() {
        aMapLocation.start(location -> {
            if (location != null) {
                if (location.getErrorCode() == 0) {
                    MineApp.cityName = location.getCity();
                    String address = location.getAddress();
                    String longitude = location.getLongitude() + "";
                    String latitude = location.getLatitude() + "";

                    PreferenceUtil.saveStringValue(activity, "longitude", longitude);
                    PreferenceUtil.saveStringValue(activity, "latitude", latitude);
                    PreferenceUtil.saveStringValue(activity, "provinceName", location.getProvince());
                    PreferenceUtil.saveStringValue(activity, "cityName", MineApp.cityName);
                    PreferenceUtil.saveStringValue(activity, "districtName", location.getDistrict());
                    PreferenceUtil.saveStringValue(activity, "address", address);
                }
                aMapLocation.stop();
                aMapLocation.destroy();
            }
        });
    }

    private void baseLocation() {
        PreferenceUtil.saveStringValue(activity, "longitude", "120.641956");
        PreferenceUtil.saveStringValue(activity, "latitude", "28.021994");
        PreferenceUtil.saveStringValue(activity, "cityName", "温州市");
        PreferenceUtil.saveStringValue(activity, "address", "浙江省温州市鹿城区望江东路175号靠近温州银行(文化支行)");
    }
}
