package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.MainVMInterface;

public class MainViewModel extends BaseViewModel implements MainVMInterface {

    @Override
    public void selectSex(int sex) {
        MineApp.registerInfo.setSex(sex);
        ActivityUtils.getRegisterNick();
        activity.finish();
    }

    @Override
    public void toLogin(View view) {
        ActivityUtils.getRegisterPhone(true);
        activity.finish();
    }

    @Override
    public void toRule(View view) {
        ActivityUtils.getMineWeb("隐私服务条款", HttpManager.BASE_URL+"mobile/yuenar_reg_protocol.html");
    }
}
