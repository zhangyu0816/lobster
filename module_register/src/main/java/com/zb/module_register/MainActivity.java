package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.vm.MainViewModel;

@Route(path = RouteUtils.Register_Main)
public class MainActivity extends BaseActivity {

    @Override
    public int getRes() {
        return R.layout.register_main;
    }

    @Override
    public void initUI() {
        mBinding.setVariable(BR.viewModel, new MainViewModel());
    }
}
