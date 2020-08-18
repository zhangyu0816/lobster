package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.api.loginCaptchaApi;
import com.zb.lib_base.api.registerCaptchaApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_register.databinding.RegisterPhoneBinding;
import com.zb.module_register.iv.PhoneVMInterface;

import androidx.databinding.ViewDataBinding;

public class PhoneViewModel extends BaseViewModel implements PhoneVMInterface {

    public boolean isLogin = false;
    private RegisterPhoneBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (RegisterPhoneBinding) binding;
    }

    @Override
    public void back(View view) {
        super.back(view);
        if (MineApp.isLogin && isLogin) {
            activity.finish();
        } else {
            if (isLogin) {
                ActivityUtils.getRegisterMain();
            } else {
                ActivityUtils.getRegisterBirthday();
            }
            activity.finish();
        }
    }

    @Override
    public void right(View view) {
        super.right(view);
        String phone = mBinding.getPhone();
        if (!phone.matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "手机号不正确", false);
            return;
        }
        hintKeyBoard();
        MineApp.registerInfo.setPhone(phone);
        ActivityUtils.getRegisterLogin();
        activity.finish();
    }

    @Override
    public void next(View view) {
        if (mBinding.getPhone().length() < 11) {
            return;
        }
        hintKeyBoard();
        if (!mBinding.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "手机号不正确", false);
            return;
        }
        // 验证码
        if (isLogin) {
            loginCaptcha();
        } else {
            registerCaptcha();
        }
    }

    @Override
    public void registerCaptcha() {
        // 注册验证码
        registerCaptchaApi api = new registerCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "验证码已发送，请注意查收", false);
                MineApp.registerInfo.setPhone(mBinding.getPhone());
                ActivityUtils.getRegisterCode(isLogin);
                activity.finish();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 12) {
                    new TextPW(activity, mBinding.getRoot(), "温馨提示", "该手机号已注册过，是否前往登录？", "去登录", new TextPW.CallBack() {
                        @Override
                        public void sure() {
                            isLogin = true;
                            mBinding.setRemark("");
                            mBinding.setRight("密码登录");
                            mBinding.setBtnName("获取登录验证码");
                            loginCaptcha();
                        }
                    });
                }
            }
        }, activity)
                .setUserName(mBinding.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void loginCaptcha() {
        loginCaptchaApi api = new loginCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "验证码已发送，请注意查收", false);
                MineApp.registerInfo.setPhone(mBinding.getPhone());
                ActivityUtils.getRegisterCode(isLogin);
                activity.finish();
            }
        }, activity).setUserName(mBinding.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
