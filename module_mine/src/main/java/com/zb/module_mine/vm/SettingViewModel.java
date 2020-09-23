package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.humanFaceStatusApi;
import com.zb.lib_base.api.loginOutApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.databinding.MineSettingBinding;
import com.zb.module_mine.iv.SettingVMInterface;
import com.zb.module_mine.views.DoubleHeadedDragonBar;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class SettingViewModel extends BaseViewModel implements SettingVMInterface {
    private BaseReceiver updateWalletReceiver;
    private MineSettingBinding mBinding;
    private List<String> selectList = new ArrayList<>();
    private AMapLocation aMapLocation;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        aMapLocation = new AMapLocation(activity);
        mBinding = (MineSettingBinding) binding;
        mBinding.setIsThreeLogin(PreferenceUtil.readIntValue(activity, "myIsThreeLogin", 0) == 1);
        updateWalletReceiver = new BaseReceiver(activity, "lobster_updateWallet") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
            }
        };
        selectList.add("只有女士");
        selectList.add("只有男士");
        selectList.add("不限");

        mBinding.setSexName(selectList.get(MineApp.sex == -1 ? 2 : MineApp.sex));


        mBinding.rattingAge.setMinValue(MineApp.minAge);
        mBinding.rattingAge.setMaxValue(MineApp.maxAge);
        mBinding.setAgeName(MineApp.minAge + "-" + MineApp.maxAge + "+");
        mBinding.rattingAge.setCallBack(new DoubleHeadedDragonBar.DhdBarCallBack() {
            //结束触摸 按百分比返回选择值，范围 0~100
            @Override
            public void onEndTouch(float minPercentage, float maxPercentage) {
                MineApp.minAge = (int) minPercentage;
                MineApp.maxAge = (int) maxPercentage;
                mBinding.setAgeName(MineApp.minAge + "-" + MineApp.maxAge + "+");
                MineApp.noDataCount = 0;
                activity.sendBroadcast(new Intent("lobster_location"));
            }

            @Override
            public void setValue(int minValue, int maxValue) {
                super.setValue(minValue, maxValue);
                MineApp.minAge = minValue;
                MineApp.maxAge = maxValue;
                mBinding.setAgeName(MineApp.minAge + "-" + MineApp.maxAge + "+");
                PreferenceUtil.saveIntValue(activity, "myMinAge", MineApp.minAge);
                PreferenceUtil.saveIntValue(activity, "myMaxAge", MineApp.maxAge);
            }
        });

        walletAndPop();
    }

    public void onDestroy() {
        try {
            updateWalletReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toSex(View view) {
        new SelectorPW(mBinding.getRoot(), selectList, position -> {
            mBinding.setSexName(selectList.get(position));
            MineApp.sex = position == 2 ? -1 : position;
            PreferenceUtil.saveIntValue(activity, "mySex", MineApp.sex);
            MineApp.noDataCount = 0;
            activity.sendBroadcast(new Intent("lobster_location"));
        });
    }

    @Override
    public void toRealName(View view) {
        if (mBinding.getIsChecked() == -1 || mBinding.getIsChecked() == 2)
            ActivityUtils.getMineRealName();
        else if (mBinding.getIsChecked() == 0) {
            SCToastUtil.showToast(activity, "人脸信息正在审核中，请耐心等待", true);
        } else {
            SCToastUtil.showToast(activity, "人脸信息验证成功，无需再次提交", true);
        }
    }

    @Override
    public void toWallet(View view) {
        ActivityUtils.getMineWallet();
    }

    @Override
    public void toLocation(View view) {
        if (MineApp.mineInfo.getMemberType() == 1) {
            if (MineApp.vipInfoList.size() > 0)
                new TextPW(mBinding.getRoot(), "VIP特权", "位置漫游服务为VIP用户专享功能", "开通会员", ActivityUtils::getMineOpenVip);
            return;
        }
        if (PreferenceUtil.readStringValue(activity, "latitude").isEmpty()) {
            new TextPW(mBinding.getRoot(), "定位失败", "定位失败，无法选取地址，请重新定位", "重新定位", this::getPermissions);
        } else {
            ActivityUtils.getMineLocation(false);
        }
    }

    @Override
    public void toPass(View view) {
        ActivityUtils.getMineModifyPass();
    }

    @Override
    public void toNotice(View view) {
        ActivityUtils.getMineNotice();
    }

    @Override
    public void toCleanCache(View view) {
        DataCleanManager.deleteFile(activity.getCacheDir());
        mBinding.setVariable(BR.cacheSize, DataCleanManager.getCacheSize(activity.getCacheDir()));
    }

    @Override
    public void toFeedback(View view) {
        ActivityUtils.getMineFeedback();
    }

    @Override
    public void toRegisterRule(View view) {
        ActivityUtils.getMineWeb("注册协议", HttpManager.BASE_URL + "mobile/xiagu_reg_protocol.html");
    }

    @Override
    public void toRule(View view) {
        ActivityUtils.getMineWeb("隐私政策", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html");
    }

    @Override
    public void toAboutUs(View view) {
        ActivityUtils.getMineWeb("关于我们", HttpManager.BASE_URL + "mobile/xiagu_about_us.html");
    }

    @Override
    public void exit(View view) {
        loginOut();
    }

    @Override
    public void loginOut() {
        loginOutApi api = new loginOutApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                PreferenceUtil.saveStringValue(activity, "sessionId", "");
                PreferenceUtil.saveLongValue(activity, "userId", 0L);
                PreferenceUtil.saveStringValue(activity, "loginPass", "");
                BaseActivity.update();
                MineApp.exit();
                ActivityUtils.getLoginActivity(0);
                activity.finish();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void humanFaceStatus() {
        humanFaceStatusApi api = new humanFaceStatusApi(new HttpOnNextListener<FaceStatus>() {
            @Override
            public void onNext(FaceStatus o) {
                mBinding.setIsChecked(o.getIsChecked());
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.setIsChecked(-1);
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void walletAndPop() {
        walletAndPopApi api = new walletAndPopApi(new HttpOnNextListener<WalletInfo>() {
            @Override
            public void onNext(WalletInfo o) {
                MineApp.walletInfo = o;
                mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            setLocation();
        }
    }

    private void setLocation() {
        aMapLocation.start(activity, (longitude, latitude, provinceName, cityName, districtName) ->
                ActivityUtils.getMineLocation(false));
    }

    private void baseLocation() {
        PreferenceUtil.saveStringValue(activity, "longitude", "120.641956");
        PreferenceUtil.saveStringValue(activity, "latitude", "28.021994");
        PreferenceUtil.saveStringValue(activity, "cityName", "温州市");
        PreferenceUtil.saveStringValue(activity, "provinceName", "浙江省");
        PreferenceUtil.saveStringValue(activity, "districtName", "鹿城区");
        PreferenceUtil.saveStringValue(activity, "address", "浙江省温州市鹿城区望江东路175号靠近温州银行(文化支行)");
        ActivityUtils.getMineLocation(false);
    }
}
