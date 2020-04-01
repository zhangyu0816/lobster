package com.zb.module_register.vm;

import android.util.Log;
import android.view.View;

import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.MainVMInterface;

public class MainViewModel extends BaseViewModel implements MainVMInterface {

    public MainViewModel() {
    }

    @Override
    public void selectSex(int sex) {
        Log.i("sex",""+sex);
    }

    @Override
    public void toLogin(View view) {
        ActivityUtils.getRegisterLogin();
    }
}
