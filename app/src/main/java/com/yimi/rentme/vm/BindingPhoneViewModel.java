package com.yimi.rentme.vm;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;

import com.yimi.rentme.R;
import com.yimi.rentme.databinding.AcBindingPhoneBinding;
import com.yimi.rentme.iv.BindingPhoneVMInterface;
import com.zb.lib_base.api.banderCaptchaApi;
import com.zb.lib_base.api.bindingPhoneApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.ImUtils;
import com.zb.lib_base.model.ImageCaptcha;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.ImageCaptchaPW;

import androidx.databinding.ViewDataBinding;

public class BindingPhoneViewModel extends BaseViewModel implements BindingPhoneVMInterface {
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
        mBinding.setRemark("获取验证码");
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                isTimer = true;
                mBinding.setRemark(activity.getResources().getString(R.string.code_second, millisUntilFinished / 1000));
            }

            public void onFinish() {
                isTimer = false;
                mBinding.setRemark("获取验证码");
                timer.cancel();
            }
        };

        ImUtils.getInstance().setCallBackForLogin(() -> {
            ImUtils.getInstance().setCallBackForLogin(null);
            ActivityUtils.getMainActivity();
            activity.finish();
        });
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
        new ImageCaptchaPW(activity, mBinding.getRoot(), new ImageCaptchaPW.CallBack() {
            @Override
            public void success(ImageCaptcha imageCaptcha, String code) {
                banderCaptcha(imageCaptcha, code);
            }

            @Override
            public void fail() {
                mBinding.setRemark("获取验证码");
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
        bindingPhone();
    }

    @Override
    public void banderCaptcha(ImageCaptcha imageCaptcha, String code) {
        banderCaptchaApi api = new banderCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "短信验证码发送成功，请注意查看", true);
                mBinding.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, second));
                timer.start();
            }

            @Override
            public void onError(Throwable e) {
                mBinding.setRemark("获取验证码");
                timer.cancel();
            }
        }, activity).setUserName(mBinding.getPhone()).setImageCaptchaCode(code).setImageCaptchaToken(imageCaptcha.getImageCaptchaToken());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void bindingPhone() {
        bindingPhoneApi api = new bindingPhoneApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "绑定成功,请前往我的--点击头像完成个人信息", false);
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
                ImUtils.getInstance().setChat(false, activity);
            }
        }, activity);
        api.setPosition(1);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
