package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.humanFaceStatusApi;
import com.zb.lib_base.api.loginOutApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.databinding.MineSettingBinding;
import com.zb.module_mine.iv.SettingVMInterface;

import androidx.databinding.ViewDataBinding;

public class SettingViewModel extends BaseViewModel implements SettingVMInterface {
    private MineInfo mineInfo;
    private BaseReceiver updateWalletReceiver;
    private BaseReceiver humanFaceReceiver;
    private MineSettingBinding mBinding;

    @Override
    public void back(View view) {
        super.back(view);
        activity.sendBroadcast(new Intent("lobster_resumeContactNum"));
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineSettingBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        humanFaceStatus();
        mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
        updateWalletReceiver = new BaseReceiver(activity, "lobster_updateWallet") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
            }
        };

        humanFaceReceiver = new BaseReceiver(activity,"lobster_humanFace") {
            @Override
            public void onReceive(Context context, Intent intent) {
                humanFaceStatus();
            }
        };
    }

    public void onDestroy() {
        humanFaceReceiver.unregisterReceiver();
        updateWalletReceiver.unregisterReceiver();
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
        if (mineInfo.getMemberType() == 1) {
            SCToastUtil.showToast(activity, "位置漫游服务为VIP用户专享功能", true);
            return;
        }
        ActivityUtils.getMineLocation(false);
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
    public void toRule(View view) {
        ActivityUtils.getMineWeb("隐私政策", "");
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
                PreferenceUtil.saveStringValue(activity, "login_pass", "");
                BaseActivity.update();
                MineApp.isLogin = false;
                MineApp.exit();
                ActivityUtils.getRegisterMain();
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
}
