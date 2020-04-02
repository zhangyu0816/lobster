package com.zb.module_register.vm;

import android.view.View;

import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_register.iv.PhoneVMInterface;

public class PhoneViewModel extends BaseViewModel{
    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }
}
