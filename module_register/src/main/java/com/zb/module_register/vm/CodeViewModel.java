package com.zb.module_register.vm;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.loginByCaptchaApi;
import com.zb.lib_base.api.loginCaptchaApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.registerCaptchaApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterCodeBinding;
import com.zb.module_register.iv.CodeVMInterface;

import androidx.databinding.ViewDataBinding;


public class CodeViewModel extends BaseViewModel implements CodeVMInterface {

    private int second = 120;
    private RegisterCodeBinding mBinding;
    private CountDownTimer timer;
    public boolean isLogin = false;
    private boolean getCode = true;
    private LoginSampleHelper loginHelper;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (RegisterCodeBinding) binding;
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                getCode = false;
                mBinding.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, millisUntilFinished / 1000));
            }

            public void onFinish() {
                mBinding.setRemark("验证码没收到？重新试试！");
                timer.cancel();
                getCode = true;
            }
        };

        // 验证码
        if (isLogin) {
            loginCaptchaApi();
        } else {
            registerCaptcha();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterPhone(isLogin);
        if (timer != null)
            timer.cancel();
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getRegisterLogin();
        if (timer != null)
            timer.cancel();
        activity.finish();
    }

    @Override
    public void reset(View view) {
        // 注册验证码
        if (getCode)
            if (isLogin) {
                loginCaptchaApi();
            } else {
                registerCaptcha();
            }
    }

    @Override
    public void sure(View view) {
        if (mBinding.edCode.getText().toString().length() < 4) {
            return;
        }
        MineApp.registerInfo.setCaptcha(mBinding.edCode.getText().toString());

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
                PreferenceUtil.saveLongValue(activity, "userId", o.getId());
                PreferenceUtil.saveStringValue(activity, "sessionId", o.getSessionId());
                PreferenceUtil.saveStringValue(activity, "userName", o.getUserName());
                BaseActivity.update();
                timer.cancel();
                myInfo();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone())
                .setCaptcha(MineApp.registerInfo.getCaptcha());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                SCToastUtil.showToast(activity, "登录成功", true);
                mineInfoDb.saveMineInfo(o);
                if (!MineApp.isLogin) {
                    ActivityUtils.getMainActivity();
                }
                activity.sendBroadcast(new Intent("lobster_mainSelect"));
                loginHelper = LoginSampleHelper.getInstance();
                loginHelper.loginOut_Sample();
                myImAccountInfoApi();
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 阿里百川登录账号
     */
    private void myImAccountInfoApi() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginHelper.loginOut_Sample();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord());
                activity.finish();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void registerCaptcha() {
        // 注册验证码
        registerCaptchaApi api = new registerCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "验证码已发送，请注意查收", false);
                mBinding.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, second));
                timer.start();
            }
        }, activity)
                .setUserName(MineApp.registerInfo.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginCaptchaApi() {
        loginCaptchaApi api = new loginCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "验证码已发送，请注意查收", false);
                mBinding.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, second));
                timer.start();
            }
        }, activity).setUserName(MineApp.registerInfo.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
