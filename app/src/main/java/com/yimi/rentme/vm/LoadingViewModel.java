package com.yimi.rentme.vm;

import android.content.Intent;
import android.os.SystemClock;

import com.umeng.commonsdk.UMConfigure;
import com.yimi.rentme.activity.LoginVideoActivity;
import com.yimi.rentme.iv.LoadingVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RulePW;
import com.zb.module_mine.activity.MineWebActivity;

import androidx.databinding.ViewDataBinding;

public class LoadingViewModel extends BaseViewModel implements LoadingVMInterface {

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        MineApp.activity = activity;
        BaseActivity.userId = PreferenceUtil.readLongValue(activity, "userId");
        BaseActivity.sessionId = PreferenceUtil.readStringValue(activity, "sessionId");
        if (PreferenceUtil.readIntValue(activity, "ruleType1") == 0) {
            MineApp.getApp().getFixedThreadPool().execute(() -> {
                SystemClock.sleep(1000L);
                activity.runOnUiThread(() -> new RulePW(activity, mBinding.getRoot(), new RulePW.CallBack() {
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
                        intent.putExtra("title", "注册协议");
                        activity.startActivity(intent);
                    }

                    @Override
                    public void privacyRule() {
                        Intent intent = new Intent(activity, MineWebActivity.class);
                        intent.putExtra("url", HttpManager.BASE_URL + "mobile/xiagu_privacy_protocol.html");
                        intent.putExtra("title", "隐私政策");
                        activity.startActivity(intent);
                    }
                }));
            });
        } else {
            UMConfigure.preInit(MineApp.instance, "55cac14467e58e8bd7000359", null);
            activity.startActivity(new Intent(activity, LoginVideoActivity.class));
        }
    }
}
