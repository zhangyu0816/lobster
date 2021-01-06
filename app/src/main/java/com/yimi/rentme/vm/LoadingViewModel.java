package com.yimi.rentme.vm;

import android.os.CountDownTimer;

import com.yimi.rentme.iv.LoadingVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RulePW;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import androidx.databinding.ViewDataBinding;

public class LoadingViewModel extends BaseViewModel implements LoadingVMInterface {
    private CountDownTimer mCountDownTimer;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        if (PreferenceUtil.readIntValue(activity, "ruleType2") == 0) {
            mCountDownTimer = new CountDownTimer(500, 500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    new RulePW(activity, mBinding.getRoot(), 2, new RulePW.CallBack() {
                        @Override
                        public void sureBack() {
                            PreferenceUtil.saveIntValue(activity, "ruleType2", 1);
                            myInfo();
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
            myInfo();
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

    @Override
    public void myInfo() {
        if (BaseActivity.sessionId.isEmpty()) {
            ActivityUtils.getLoginVideoActivity();
            activity.finish();
        } else {
            myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
                @Override
                public void onNext(MineInfo o) {
                    MineApp.mineInfo = o;
                    ActivityUtils.getMainActivity();
                    activity.finish();
                }

                @Override
                public void onError(Throwable e) {
                    if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof UnknownHostException) {
                        ActivityUtils.getLoginVideoActivity();
                        activity.finish();
                    }
                }
            }, activity);
            api.setDialogTitle("loadingNotLogin");
            HttpManager.getInstance().doHttpDeal(api);
        }
    }
}
