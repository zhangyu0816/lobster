package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.databinding.RegisterPhoneBinding;
import com.zb.module_register.iv.PhoneVMInterface;

public class PhoneViewModel extends BaseViewModel implements PhoneVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void next(View view) {
        String phone = ((RegisterPhoneBinding) mBinding).getPhone();
        if (!phone.matches(MineApp.PHONE_NUMBER_REG)) {
            SCToastUtil.showToast(activity, "手机号不正确");
            return;
        }

        MineApp.registerInfo.setPhone(phone);

        // 调用发送短信接口后跳页面
    }
}
