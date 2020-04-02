package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.LogoVMInterface;

public class LogoViewModel extends BaseViewModel implements LogoVMInterface {
    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void next(View view) {

    }
}
