package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterMainBinding;
import com.zb.module_register.vm.MainViewModel;

@Route(path = RouteUtils.Register_Main)
public class MainActivity extends RegisterBaseActivity {

    @Override
    public int getRes() {
        return R.layout.register_main;
    }

    @Override
    public void initUI() {
        MainViewModel viewModel = new MainViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);

        RegisterMainBinding binding = (RegisterMainBinding) mBinding;
        AdapterBinding.viewSize(binding.ivBoy, ObjectUtils.getViewSizeByWidth(300f / 1080f), ObjectUtils.getViewSizeByWidth(300f / 1080f));
        AdapterBinding.viewSize(binding.ivGirl, ObjectUtils.getViewSizeByWidth(300f / 1080f), ObjectUtils.getViewSizeByWidth(300f / 1080f));
    }
}
