package com.zb.module_register.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_register.BR;
import com.zb.module_register.R;
import com.zb.module_register.vm.BindingPhoneViewModel;

@Route(path = RouteUtils.Register_Binding_Phone)
public class BindingPhoneActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.WhiteTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
    }

    @Override
    public int getRes() {
        return R.layout.register_binding_phone;
    }

    @Override
    public void initUI() {
        BindingPhoneViewModel viewModel = new BindingPhoneViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "绑定手机号");
    }
}
