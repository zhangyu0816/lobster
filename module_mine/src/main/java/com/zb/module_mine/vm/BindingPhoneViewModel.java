package com.zb.module_mine.vm;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;

import com.zb.module_mine.R;
import com.zb.module_mine.databinding.AcBindingPhoneBinding;
import com.zb.module_mine.iv.BindingPhoneVMInterface;
import com.zb.lib_base.api.banderCaptchaApi;
import com.zb.lib_base.api.bindingPhoneApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.registerCaptchaApi;
import com.zb.lib_base.api.verifyCaptchaApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ImageCaptcha;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.ImageCaptchaPW;

import androidx.databinding.ViewDataBinding;

public class BindingPhoneViewModel extends BaseViewModel implements BindingPhoneVMInterface {
    public boolean isRegister;
    public boolean isFinish;
    private AcBindingPhoneBinding mBinding;
    private int second = 120;
    private CountDownTimer timer;
    private boolean isTimer = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcBindingPhoneBinding) binding;
        mBinding.setCode("");
        mBinding.setPhone("");
        mBinding.setRemark(Html.fromHtml("获取验证码"));
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                isTimer = true;
                mBinding.setRemark(Html.fromHtml(activity.getResources().getString(R.string.code_second, millisUntilFinished / 1000)));
            }

            public void onFinish() {
                isTimer = false;
                mBinding.setRemark(Html.fromHtml("获取验证码"));
                timer.cancel();
            }
        };
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void getCode(View view) {
        if (isTimer) return;
        if (mBinding.getPhone().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入手机号", true);
            return;
        }
        if (!mBinding.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "请输入正确的手机号", true);
            return;
        }
        if (isRegister)
            registerCaptcha();
        else
            new ImageCaptchaPW(mBinding.getRoot(), new ImageCaptchaPW.CallBack() {
                @Override
                public void success(ImageCaptcha imageCaptcha, String code) {
                    banderCaptcha(imageCaptcha, code);
                }

                @Override
                public void fail() {
                    mBinding.setRemark(Html.fromHtml("获取验证码"));
                    timer.cancel();
                }
            });
    }

    @Override
    public void binding(View view) {
        if (mBinding.getPhone().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入手机号", true);
            return;
        }
        if (!mBinding.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "请输入正确的手机号", true);
            return;
        }
        if (mBinding.getCode().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入短信验证码", true);
            return;
        }
        if (isRegister) {
            verifyCaptcha();
        } else
            bindingPhone();
    }

    private void verifyCaptcha() {
        verifyCaptchaApi api = new verifyCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                Intent data = new Intent();
                data.putExtra("bindPhone", mBinding.getPhone());
                data.putExtra("captcha", mBinding.getCode());
                activity.setResult(Activity.RESULT_OK, data);
                activity.finish();
            }
        }, activity).setUserName(mBinding.getPhone()).setCaptcha(mBinding.getCode());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void banderCaptcha(ImageCaptcha imageCaptcha, String code) {
        banderCaptchaApi api = new banderCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "短信验证码发送成功，请注意查看", true);
                mBinding.setRemark(Html.fromHtml(MineApp.getApp().getResources().getString(R.string.code_second, second)));
                timer.start();
            }

            @Override
            public void onError(Throwable e) {
                mBinding.setRemark(Html.fromHtml("获取验证码"));
                timer.cancel();
            }
        }, activity).setUserName(mBinding.getPhone()).setImageCaptchaCode(code).setImageCaptchaToken(imageCaptcha.getImageCaptchaToken());
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 注册验证码
     */
    private void registerCaptcha() {
        registerCaptchaApi api = new registerCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "短信验证码发送成功，请注意查看", true);
                mBinding.setRemark(Html.fromHtml(MineApp.getApp().getResources().getString(R.string.code_second, second)));
                timer.start();
            }

            @Override
            public void onError(Throwable e) {
                mBinding.setRemark(Html.fromHtml("获取验证码"));
                timer.cancel();
            }
        }, activity).setUserName(mBinding.getPhone());
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void bindingPhone() {
        bindingPhoneApi api = new bindingPhoneApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.setResult(Activity.RESULT_OK);
                myInfo();
            }
        }, activity).setUserName(mBinding.getPhone()).setCaptcha(mBinding.getCode());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                if (!isFinish)
                    ActivityUtils.getMainActivity();
                activity.finish();
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
