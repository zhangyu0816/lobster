package com.zb.module_mine.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.igexin.sdk.PushManager;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.deleteUserApi;
import com.zb.lib_base.api.humanFaceStatusApi;
import com.zb.lib_base.api.loginOutApi;
import com.zb.lib_base.api.realNameVerifyApi;
import com.zb.lib_base.api.setSendMessageApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.OpenNotice;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SettingViewModel extends BaseViewModel implements SettingVMInterface {
    private BaseReceiver updateWalletReceiver;
    private MineSettingBinding mBinding;
    private List<String> selectList = new ArrayList<>();
    private AMapLocation aMapLocation;
    private Authentication mAuthentication;

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
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_location"));
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
        setSendMessageApi(-1);
    }

    public void onResume() {
        if (OpenNotice.isNotNotification(activity)) {
            PushManager.getInstance().turnOffPush(MineApp.instance);
        } else {
            PushManager.getInstance().initialize(MineApp.instance);
            PushManager.getInstance().turnOnPush(MineApp.instance);
        }
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
            LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_location"));
        });
    }

    @Override
    public void toRealName(View view) {
        if (mBinding == null) return;
        if (mBinding.getIsChecked() == -1 || mBinding.getIsChecked() == 2) {
            if (PreferenceUtil.readIntValue(activity, "realPermission") == 0) {
                ActivityUtils.getMineRealName();
            } else if (checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                ActivityUtils.getMineRealName();
            } else
                SCToastUtil.showToast(activity, "你未申请相机权限，请前往设置--权限管理--权限进行设置", true);
        } else if (mBinding.getIsChecked() == 0) {
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
                new TextPW(mBinding.getRoot(), "VIP特权", "位置漫游服务为VIP用户专享功能", "开通会员", () -> ActivityUtils.getMineOpenVip(false));
            return;
        }
        if (PreferenceUtil.readStringValue(activity, "latitude").equals("0")) {
            new TextPW(mBinding.getRoot(), "定位失败", "定位失败，无法选取地址，请重新定位", "重新定位", () -> {
                if (checkPermissionGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    CustomProgressDialog.showLoading(activity, "定位...");
                    setLocation(0);
                } else {
                    SCToastUtil.showToast(activity, "你未申请定位权限，请前往设置--权限管理--权限进行设置", true);
                }
            });
        } else {
            if (checkPermissionGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
                setLocation(1);
            } else {
                SCToastUtil.showToast(activity, "你未申请定位权限，请前往设置--权限管理--权限进行设置", true);
            }
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
    public void toNoticeManager(View view) {
        OpenNotice.gotoSet(activity);
    }

    @Override
    public void closeUseType(View view) {
        setSendMessageApi(mBinding.getUseType() == 0 ? 1 : 0);
    }

    @Override
    public void toIdCard(View view) {
        if (mAuthentication == null) {
            if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                setPermissions();
            } else {
                if (PreferenceUtil.readIntValue(activity, "realNamePermission") == 0)
                    new TextPW(activity, mBinding.getRoot(), "实名认证",
                            "您在使用提现服务时，为确认您所提供的身份信息、提现账号（银行卡信息或支付宝信息）为您本人信息且属实，需要您提供以下信息供我们收集：真实姓名、身份证号及身份证核验授权，我们将会申请相机、存储权限：" +
                                    "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                    "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                    "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，上传身份证正反面照片及本人头像照片，" +
                                    "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法完成身份证认证，不影响使用其他的虾菇功能/服务，" +
                                    "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限，" +
                                    "\n 7、先在卡包内绑定提现账户，单次提现金额不少于10元。提交提现申请后，我们将会在3个工作日内打款至提现账户。",
                            "同意", false, true, new TextPW.CallBack() {
                        @Override
                        public void sure() {
                            PreferenceUtil.saveIntValue(activity, "realNamePermission", 1);
                            getPermissions();
                        }

                        @Override
                        public void cancel() {
                            PreferenceUtil.saveIntValue(activity, "realNamePermission", 1);
                        }
                    });
                else {
                    if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                    } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                        SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                    }
                }
            }
        } else if (mAuthentication.getIsChecked() == 1) {
            SCToastUtil.showToast(activity, "实名认证已通过，无需再次审核", true);
        } else {
            if (mAuthentication.getIsChecked() == 0 || mAuthentication.getIsChecked() == 100) {
                new TextPW(mBinding.getRoot(), "实名认证", "实名认证还在审核中，请稍后再试！");
            } else {
                ActivityUtils.getMineAuthentication(mAuthentication);
            }
        }
    }
    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "realNamePermission", 1);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getMineAuthentication(new Authentication());
    }
    @Override
    public void toBindingPhone(View view) {
        ActivityUtils.getBindingPhoneActivity(activity, false, true);
    }

    @Override
    public void toPermission(View view) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    public void realNameVerify() {
        realNameVerifyApi api = new realNameVerifyApi(new HttpOnNextListener<Authentication>() {
            @Override
            public void onNext(Authentication o) {
                mAuthentication = o;
                if (mAuthentication.getIsChecked() == 1) {
                    mBinding.tvCard.setText("认证通过");
                    mBinding.tvCard.setTextColor(Color.parseColor("#37A0FF"));
                } else {
                    if (mAuthentication.getIsChecked() == 0 || mAuthentication.getIsChecked() == 100) {
                        mBinding.tvCard.setText("审核中");
                        mBinding.tvCard.setTextColor(Color.parseColor("#999999"));
                    } else {
                        mBinding.tvCard.setText("审核失败");
                        mBinding.tvCard.setTextColor(Color.parseColor("#FF3158"));
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void setSendMessageApi(int type) {
        setSendMessageApi api = new setSendMessageApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                mBinding.setUseType(o);
            }
        }, activity).setUseType(type);
        HttpManager.getInstance().doHttpDeal(api);
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
                MineApp.sMIMCUser.logout();
                MineApp.sMIMCUser.destroy();
                ActivityUtils.getLoginVideoActivity();
                MineApp.getApp().exit();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void deleteUser(View view) {
        new TextPW(activity, mBinding.getRoot(), "注销账号", "注销后，将清除账号一切信息，不再出现在应用内！", "注销", true, new TextPW.CallBack() {
            @Override
            public void sure() {
                deleteUserApi api = new deleteUserApi(new HttpOnNextListener<String>() {
                    @Override
                    public void onNext(String o) {
                        PreferenceUtil.saveStringValue(activity, "sessionId", "");
                        PreferenceUtil.saveLongValue(activity, "userId", 0L);
                        PreferenceUtil.saveStringValue(activity, "loginPass", "");
                        BaseActivity.update();
                        MineApp.sMIMCUser.logout();
                        MineApp.sMIMCUser.destroy();
                        ActivityUtils.getLoginVideoActivity();
                        MineApp.getApp().exit();
                        activity.finish();
                    }
                }, activity);
                HttpManager.getInstance().doHttpDeal(api);
            }
        });
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

    private void setLocation(int type) {
        if (type == 0)
            aMapLocation.start(activity, () -> {
                        CustomProgressDialog.stopLoading();
                        ActivityUtils.getMineLocation(false);
                    }
            );
        else
            ActivityUtils.getMineLocation(false);
    }
}
