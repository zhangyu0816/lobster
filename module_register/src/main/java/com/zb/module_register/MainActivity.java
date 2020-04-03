package com.zb.module_register;

import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.app.MineApp;
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

        mBinding.setVariable(BR.viewModel, new MainViewModel());

        RegisterMainBinding binding = (RegisterMainBinding) mBinding;

        ViewGroup.LayoutParams lp = binding.ivBoy.getLayoutParams();
        lp.height = lp.width = (int) (300f * MineApp.W / 1080f);
        binding.ivBoy.setLayoutParams(lp);
        binding.ivGirl.setLayoutParams(lp);
    }
}
