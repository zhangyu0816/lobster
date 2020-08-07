package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterPhoneBinding;
import com.zb.module_register.iv.PhoneVMInterface;

import androidx.databinding.ViewDataBinding;

public class PhoneViewModel extends BaseViewModel implements PhoneVMInterface {

    public boolean isLogin = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
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
        String phone = ((RegisterPhoneBinding) mBinding).getPhone();
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
        String phone = ((RegisterPhoneBinding) mBinding).getPhone();
        if (phone.length() < 11) {
            return;
        }
        hintKeyBoard();
        if (!phone.matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "手机号不正确", false);
            return;
        }
        MineApp.registerInfo.setPhone(phone);
        ActivityUtils.getRegisterCode(isLogin);
        activity.finish();
    }
}
