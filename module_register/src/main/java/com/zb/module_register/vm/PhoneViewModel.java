package com.zb.module_register.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.Mac;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterPhoneBinding;
import com.zb.module_register.iv.PhoneVMInterface;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.ActivityCompat;
import androidx.databinding.ViewDataBinding;

public class PhoneViewModel extends BaseViewModel implements PhoneVMInterface {
    public boolean isLogin = false;
    private TelephonyManager tm;
    private long exitTime = 0;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        getPermissions();
    }

    @Override
    public void back(View view) {
        super.back(view);
        if (MineApp.isLogin && isLogin) {
            activity.finish();
        } else {
            if (isLogin) {
                ActivityUtils.getRegisterMain();
            } else {
                ActivityUtils.getRegisterBirthday();
            }
            activity.finish();
        }
    }

    @SuppressLint("HardwareIds")
    private void initPhone() {
        tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PreferenceUtil.saveStringValue(activity, "deviceCode", tm.getDeviceId());
        String iccid = tm.getSimSerialNumber();
        // CDMA手机返回MEID
        String meid = tm.getDeviceId();
        // GSM手机返回IMEI
        String imei = tm.getDeviceId();
        String mac = Mac.getMac(activity);

        JSONObject object = new JSONObject();
        try {
            object.put("iccid", iccid);
            object.put("meid", meid);
            object.put("imei", imei);
            object.put("mac", mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PreferenceUtil.saveStringValue(activity, "deviceHardwareInfo", object.toString());
    }

    @Override
    public void next(View view) {
        String phone = ((RegisterPhoneBinding) mBinding).getPhone();
        if (!phone.matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "手机号不正确");
            return;
        }
        MineApp.registerInfo.setPhone(phone);
        ActivityUtils.getRegisterCode(isLogin);
        activity.finish();
    }


    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要手机设备权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                    back(null);
                }
            }, Manifest.permission.READ_PHONE_STATE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        initPhone();
    }
}
