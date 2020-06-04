package com.zb.module_mine.vm;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.zb.lib_base.api.findPassCaptchaApi;
import com.zb.lib_base.api.findPassWordApi;
import com.zb.lib_base.api.modifyPassApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ImageCaptcha;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineModifyPassBinding;
import com.zb.module_mine.iv.ModifyPassVMInterface;
import com.zb.module_mine.windows.ImageCaptchaPW;

import androidx.databinding.ViewDataBinding;

public class ModifyPassViewModel extends BaseViewModel implements ModifyPassVMInterface {
    private MineModifyPassBinding mBinding;
    private int second = 120;
    private CountDownTimer timer;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineModifyPassBinding) binding;
        mBinding.setRemark("获取验证码");
        timer = new CountDownTimer(second * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, millisUntilFinished / 1000));
            }

            public void onFinish() {
                mBinding.setRemark("获取验证码");
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
    public void select(int index) {
        if (index == 1) {
            mBinding.setShowNewPass(!mBinding.getShowNewPass());
            mBinding.edNew.setTransformationMethod(mBinding.getShowNewPass() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        } else {
            mBinding.setShowSurePass(!mBinding.getShowSurePass());
            mBinding.edSure.setTransformationMethod(mBinding.getShowSurePass() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void modify(View view) {

        if (mBinding.getType() == 1) {
            if (mBinding.getOldPass().isEmpty()) {
                SCToastUtil.showToastBlack(activity, "请输入旧密码");
                return;
            }
            if (!TextUtils.equals(mBinding.getOldPass(), PreferenceUtil.readStringValue(activity, "login_pass"))) {
                SCToastUtil.showToastBlack(activity, "旧密码错误");
                return;
            }
        } else {
            if (mBinding.getPhone().isEmpty()) {
                SCToastUtil.showToastBlack(activity, "请输入手机号");
                return;
            }
            if (!mBinding.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
                SCToastUtil.showToastBlack(activity, "请输入正确的手机号");
                return;
            }
            if (mBinding.getCode().isEmpty()) {
                SCToastUtil.showToastBlack(activity, "请输入短信验证码");
                return;
            }
        }

        if (mBinding.getNewPass().length() < 6) {
            SCToastUtil.showToastBlack(activity, "请输入至少6位新密码");
            return;
        }
        if (!TextUtils.equals(mBinding.getNewPass(), mBinding.getSurePass())) {
            SCToastUtil.showToastBlack(activity, "确认密码输入错误");
            return;
        }

        if (mBinding.getType() == 1) {
            modifyPassApi api = new modifyPassApi(new HttpOnNextListener() {
                @Override
                public void onNext(Object o) {
                    PreferenceUtil.saveStringValue(activity, "login_pass", mBinding.getNewPass());
                    SCToastUtil.showToastBlack(activity, "修改成功");
                    activity.finish();
                }
            }, activity).setOldPassWord(mBinding.getOldPass()).setNewPassWord(mBinding.getNewPass());
            HttpManager.getInstance().doHttpDeal(api);
        } else {
            findPassWord();
        }
    }

    @Override
    public void changeType(View view) {
        mBinding.setType(mBinding.getType() == 1 ? 2 : 1);
    }

    @Override
    public void getCode(View view) {
        if (mBinding.getPhone().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入手机号");
            return;
        }
        if (!mBinding.getPhone().matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToastBlack(activity, "请输入正确的手机号");
            return;
        }
        new ImageCaptchaPW(activity, mBinding.getRoot(), new ImageCaptchaPW.CallBack() {
            @Override
            public void success(ImageCaptcha imageCaptcha, String code) {
                mBinding.setRemark(MineApp.getInstance().getResources().getString(R.string.code_second, second));
                timer.start();
                findPassCaptcha(imageCaptcha, code);
            }

            @Override
            public void fail() {
                mBinding.setRemark("获取验证码");
                timer.cancel();
            }
        });
    }

    @Override
    public void findPassCaptcha(ImageCaptcha imageCaptcha, String code) {
        findPassCaptchaApi api = new findPassCaptchaApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "短信验证码发送成功，请注意查看");
                mBinding.setRemark("获取验证码");
                timer.cancel();
            }
        }, activity).setUserName(mBinding.getPhone()).setImageCaptchaCode(code).setImageCaptchaToken(imageCaptcha.getImageCaptchaToken());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void findPassWord() {
        findPassWordApi api = new findPassWordApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                PreferenceUtil.saveStringValue(activity, "login_pass", mBinding.getNewPass());
                SCToastUtil.showToastBlack(activity, "修改成功");
                activity.finish();
            }
        }, activity).setCaptcha(mBinding.getCode()).setUserName(mBinding.getPhone()).setPassWord(mBinding.getNewPass());
        HttpManager.getInstance().doHttpDeal(api);
    }
}
