package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.LoginVMInterface;

public class LoginViewModel extends BaseViewModel implements LoginVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        ActivityUtils.getRegisterPhone(true);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getRegisterCode(true);
        activity.finish();
    }

    @Override
    public void complete(View view) {
        SCToastUtil.showToast(activity,"登录成功");
        activity.finish();
    }
}
