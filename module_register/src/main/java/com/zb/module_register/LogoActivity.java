package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.vm.LogoViewModel;

@Route(path = RouteUtils.Register_Logo)
public class LogoActivity extends BaseActivity {

    private LogoViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.register_logo;
    }

    @Override
    public void initUI() {
        viewModel = new LogoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
