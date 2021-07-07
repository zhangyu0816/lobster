package com.yimi.rentme.vm;

import android.content.Intent;
import android.os.CountDownTimer;

import com.yimi.rentme.iv.LoadingVMInterface;
import com.yimi.rentme.service.ForegroundLiveService;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RulePW;

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
                            activity.startService(new Intent(activity, ForegroundLiveService.class));
                            PreferenceUtil.saveIntValue(activity, "ruleType1", 1);
                            ActivityUtils.getLoginVideoActivity();
                        }

                        @Override
                        public void cancelBack() {
                            activity.finish();
                        }
                    });
                }
            };
            mCountDownTimer.start();
        } else {
            activity.startService(new Intent(activity, ForegroundLiveService.class));
            ActivityUtils.getLoginVideoActivity();
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
