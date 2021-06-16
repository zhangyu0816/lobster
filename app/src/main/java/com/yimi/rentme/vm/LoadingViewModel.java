package com.yimi.rentme.vm;

import android.os.CountDownTimer;

import com.yimi.rentme.iv.LoadingVMInterface;
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
        if (PreferenceUtil.readIntValue(activity, "ruleType1") == 0) {
            mCountDownTimer = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    new RulePW(activity, mBinding.getRoot(),  new RulePW.CallBack() {
                        @Override
                        public void sureBack() {
                            PreferenceUtil.saveIntValue(activity, "ruleType1", 1);
                            ActivityUtils.getLoginVideoActivity();
                            activity.finish();
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
            ActivityUtils.getLoginVideoActivity();
            activity.finish();
//            ActivityUtils.getCameraPhotoStudio();
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
