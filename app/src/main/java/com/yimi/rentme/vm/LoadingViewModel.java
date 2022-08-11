package com.yimi.rentme.vm;

import android.content.Intent;
import android.os.CountDownTimer;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.debug.I;
import com.yimi.rentme.activity.LoginVideoActivity;
import com.yimi.rentme.iv.LoadingVMInterface;
import com.yimi.rentme.service.ForegroundLiveService;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RulePW;
import com.zb.module_mine.activity.MineWebActivity;

import androidx.databinding.ViewDataBinding;

public class LoadingViewModel extends BaseViewModel implements LoadingVMInterface {
    private CountDownTimer mCountDownTimer;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        MineApp.activity = activity;
        if (PreferenceUtil.readIntValue(activity, "ruleType1") == 0) {
            mCountDownTimer = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    new RulePW(activity, mBinding.getRoot(), new RulePW.CallBack() {
                        @Override
                        public void sureBack() {
                            UMConfigure.preInit(MineApp.instance, "55cac14467e58e8bd7000359", null);
                            PreferenceUtil.saveIntValue(activity, "ruleType1", 1);
                            activity.startActivity(new Intent(activity, LoginVideoActivity.class));
                        }

                        @Override
                        public void cancelBack() {
                            activity.finish();
                        }

                        @Override
                        public void registerRule() {
                            Intent intent = new Intent(activity, MineWebActivity.class);
                            intent.putExtra("url", HttpManager.BASE_URL + "mobile/xiagu_reg_protocol.html");
                            intent.putExtra("title","注册协议");
                            activity.startActivity(intent);
                        }

                        @Override
                        public void privacyRule() {
                            Intent intent = new Intent(activity, MineWebActivity.class);
                            intent.putExtra("url", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html");
                            intent.putExtra("title","隐私政策");
                            activity.startActivity(intent);
                        }
                    });
                }
            };
            mCountDownTimer.start();
        } else {
            UMConfigure.preInit(MineApp.instance, "55cac14467e58e8bd7000359", null);
            activity.startActivity(new Intent(activity, LoginVideoActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = null;
    }
}
