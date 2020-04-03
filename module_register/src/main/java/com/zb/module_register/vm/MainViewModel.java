package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.MainVMInterface;

public class MainViewModel extends BaseViewModel implements MainVMInterface {

    @Override
    public void selectSex(int sex) {
        MineApp.registerInfo.setSex(sex);
        ActivityUtils.getRegisterNick();
    }

    @Override
    public void toLogin(View view) {
//        ActivityUtils.getRegisterLogin();
        ActivityUtils.getRegisterLogo();
//        ActivityUtils.getCameraMain();
    }
}
