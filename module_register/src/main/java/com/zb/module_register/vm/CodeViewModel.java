package com.zb.module_register.vm;

import android.os.CountDownTimer;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.loginByCaptchaApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterCodeBinding;
import com.zb.module_register.iv.CodeVMInterface;

import androidx.databinding.ViewDataBinding;


public class CodeViewModel extends BaseViewModel implements CodeVMInterface {

    private int second = 60;
    private RegisterCodeBinding bindings;
    private CountDownTimer timer;
    public boolean isLogin = false;
    private boolean getCode = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        bindings = (RegisterCodeBinding) binding;
        bindings.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, second));
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                bindings.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, millisUntilFinished / 1000));
            }

            public void onFinish() {
                bindings.setRemark("验证码没收到？重新试试！");
                timer.cancel();
                getCode = true;
            }
        };
        timer.start();
        // 注册验证码
        registerCaptcha();
    }

    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterPhone(isLogin);
        timer.cancel();
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getRegisterLogin();
        timer.cancel();
        activity.finish();
    }

    @Override
    public void reset(View view) {
        timer.start();
        // 注册验证码
        if (getCode)
            registerCaptcha();
    }

    @Override
    public void sure(View view) {
        if (bindings.edCode.getText().toString().length() < 4) {
            SCToastUtil.showToast(activity, "请输入4位有效验证码");
            return;
        }
        MineApp.registerInfo.setCaptcha(bindings.edCode.getText().toString());

        if (isLogin) {
            loginByCaptcha();
        } else {
            ActivityUtils.getRegisterLogo();
            timer.cancel();
            activity.finish();
        }
    }

    @Override
    public void loginByCaptcha() {
        loginByCaptchaApi api = new loginByCaptchaApi(new HttpOnNextListener<LoginInfo>() {
            @Override
            public void onNext(LoginInfo o) {
                SCToastUtil.showToast(activity, "登录成功");
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                BaseActivity.update();
                timer.cancel();
                ActivityUtils.getMainActivity();
                activity.finish();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setCaptcha(MineApp.registerInfo.getCaptcha());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
