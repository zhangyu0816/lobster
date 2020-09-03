package com.zb.module_register.vm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.loginByUnionApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RegisterInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.Mac;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RulePW;
import com.zb.module_register.databinding.RegisterMainBinding;
import com.zb.module_register.iv.MainVMInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import androidx.core.app.ActivityCompat;
import androidx.databinding.ViewDataBinding;

public class MainViewModel extends BaseViewModel implements MainVMInterface {

    private AMapLocation aMapLocation;
    private BaseReceiver bindPhoneReceiver;
    private TelephonyManager tm;
    private String openId;
    private String unionId;
    private String unionNick;
    private String unionImage;
    private int unionSex;
    private int unionType;
    private UMShareAPI umShareAPI;

    private LoginSampleHelper loginHelper;
    private RegisterMainBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (RegisterMainBinding) binding;
        mBinding.setIsThree(false);
        MineApp.registerInfo = new RegisterInfo();
        aMapLocation = new AMapLocation(activity);
        MineApp.cityName = PreferenceUtil.readStringValue(activity, "cityName");
        getPermissions();

        umShareAPI = UMShareAPI.get(activity);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        umShareAPI.setShareConfig(config);

        bindPhoneReceiver = new BaseReceiver(activity, "lobster_bindPhone") {
            @Override
            public void onReceive(Context context, Intent intent) {
                MineApp.registerInfo.setOpenId(openId);
                MineApp.registerInfo.setUnionId(unionId);
                MineApp.registerInfo.setUnionType(unionType);
                MineApp.registerInfo.setName(unionNick);
                MineApp.registerInfo.getImageList().add(unionImage);
                mBinding.setIsThree(true);
            }
        };
    }

    public void onDestroy() {
        bindPhoneReceiver.unregisterReceiver();
    }

    @Override
    public void selectSex(int sex) {
        MineApp.registerInfo.setSex(sex);
        showRule(1);
    }

    @Override
    public void toLogin(View view) {
        showRule(2);
    }

    private long exitTime = 0;

    @Override
    public void changeUrl(View view) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            exitTime = System.currentTimeMillis();
        } else {
            exitTime = 0;
            HttpManager.BASE_URL = TextUtils.equals(HttpManager.BASE_URL, "http://192.168.1.88:8090/") ? "https://xgapi.zuwo.la/" : "http://192.168.1.88:8090/";
            HttpManager.INSTANCE = null;

            SCToastUtil.showToast(activity, HttpManager.BASE_URL, true);
        }
    }

    @Override
    public void loginByUnion() {
        loginByUnionApi api = new loginByUnionApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", "");
                PreferenceUtil.saveStringValue(activity, "loginPass", "");
                BaseActivity.update();
                MineApp.isThreeLogin = true;
                PreferenceUtil.saveIntValue(activity, "myIsThreeLogin", 1);
                if (o.getPhoneNum().isEmpty()) {
                    ActivityUtils.getBindingPhone(activity);
                } else {
                    myInfo();
                }
            }
        }, activity)
                .setOpenId(openId)
                .setUnionId(unionId)
                .setUnionImage(unionImage)
                .setUnionNick(unionNick)
                .setUnionSex(unionSex)
                .setUnionType(unionType);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toQQ(View view) {
        unionType = 2;
        CustomProgressDialog.showLoading(activity, "正在QQ登录申请权限");
        umShareAPI.getPlatformInfo(activity, SHARE_MEDIA.QQ, umAuthListener);
    }

    @Override
    public void toWX(View view) {
        unionType = 1;
        CustomProgressDialog.showLoading(activity, "正在微信登录申请权限");
        umShareAPI.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, umAuthListener);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int status, Map<String, String> info) {
            CustomProgressDialog.showLoading(activity, "授权成功，正在登录中...");
            if (status == 2) {
                unionNick = "";
                unionImage = "";
                unionSex = 0;
                openId = "";
                unionId = "";
                if ("QQ".equals(platform.name())) {
                    unionNick = stringFilter(info.get("screen_name"));
                    unionImage = info.get("profile_image_url");
                    String sexName = info.get("gender");
                    if ("女".equals(sexName)) {
                        unionSex = 0;
                    } else {
                        unionSex = 1;
                    }
                    openId = info.get("openid");
                    unionId = "";
                } else if ("WEIXIN".equals(platform.name())) {
                    unionNick = stringFilter(info.get("screen_name"));
                    unionImage = info.get("profile_image_url");
                    String sexName = info.get("gender");
                    if ("女".equals(sexName)) {
                        unionSex = 0;
                    } else {
                        unionSex = 1;
                    }
                    openId = info.get("openid");
                    unionId = info.get("unionid");
                }

                try {
                    loginByUnion();
                } catch (Exception e) {
                    CustomProgressDialog.stopLoading();
                    SCToastUtil.showToast(activity, "获取用户信息失败", true);
                }
            } else {
                CustomProgressDialog.stopLoading();
                SCToastUtil.showToast(activity, "获取用户信息失败", true);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            CustomProgressDialog.stopLoading();
            SCToastUtil.showToast(activity, "获取用户信息失败", true);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            CustomProgressDialog.stopLoading();
        }
    };

    public static String stringFilter(String str) throws PatternSyntaxException {
        String regEx = ".*\\p{So}.*";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                SCToastUtil.showToast(activity, "登录成功", false);
                mineInfoDb.saveMineInfo(o);
                loginHelper = LoginSampleHelper.getInstance();
                loginHelper.loginOut_Sample();
                myImAccountInfoApi();
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 阿里百川登录账号
     */
    private void myImAccountInfoApi() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginHelper.loginOut_Sample();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord());
                if (MineApp.isLogin) {
                    activity.sendBroadcast(new Intent("lobster_mainSelect"));
                } else {
                    ActivityUtils.getMainActivity();
                }
                activity.finish();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void showRule(int type) {
        if (PreferenceUtil.readIntValue(activity, "ruleType1") == 0) {
            new Handler().postDelayed(() -> new RulePW(activity, mBinding.getRoot(), 1, new RulePW.CallBack() {
                @Override
                public void sureBack() {
                    PreferenceUtil.saveIntValue(activity, "ruleType1", 1);
                    if (type == 1) {
                        ActivityUtils.getRegisterNick();
                        activity.finish();
                    } else if (type == 2) {
                        ActivityUtils.getRegisterPhone(true);
                        activity.finish();
                    }
                }

                @Override
                public void cancelBack() {

                }
            }), 200);
        } else {
            if (type == 1) {
                ActivityUtils.getRegisterNick();
                activity.finish();
            } else if (type == 2) {
                ActivityUtils.getRegisterPhone(true);
                activity.finish();
            }
        }
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问定位权限及手机权限", new BaseActivity.PermissionCallback() {
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
        initPhone();
        if (MineApp.cityName.isEmpty()) {
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
    }

    private void baseLocation() {
        PreferenceUtil.saveStringValue(activity, "longitude", "120.641956");
        PreferenceUtil.saveStringValue(activity, "latitude", "28.021994");
        PreferenceUtil.saveStringValue(activity, "cityName", "温州市");
        PreferenceUtil.saveStringValue(activity, "address", "浙江省温州市鹿城区望江东路175号靠近温州银行(文化支行)");
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

}
