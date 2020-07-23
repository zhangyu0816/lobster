package com.yimi.rentme.vm;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.yimi.rentme.iv.LoadingVMInterface;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.RulePW;
import com.zb.lib_base.windows.TextPW;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import androidx.databinding.ViewDataBinding;

public class LoadingViewModel extends BaseViewModel implements LoadingVMInterface {
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        if (PreferenceUtil.readIntValue(activity, "ruleType2") == 0) {
            new Handler().postDelayed(() -> new RulePW(activity, mBinding.getRoot(), 2, new RulePW.CallBack() {
                @Override
                public void sureBack() {
                    PreferenceUtil.saveIntValue(activity, "ruleType2", 1);
                    myInfo();
                }

                @Override
                public void cancelBack() {
                    activity.finish();
                }
            }), 200);
        } else {
            myInfo();
        }
    }

    @Override
    public void myInfo() {
        if (BaseActivity.sessionId.isEmpty()) {
            ActivityUtils.getRegisterMain();
            activity.finish();
        } else {
            myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
                @Override
                public void onNext(MineInfo o) {
                    mineInfoDb.saveMineInfo(o);
                    ActivityUtils.getMainActivity();
                    activity.finish();
                }

                @Override
                public void onError(Throwable e) {
                    if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof UnknownHostException) {
                        ActivityUtils.getRegisterMain();
                        activity.finish();
                    }
                }
            }, activity);
            api.setDialogTitle("loadingNotLogin");
            HttpManager.getInstance().doHttpDeal(api);
        }
    }
}
