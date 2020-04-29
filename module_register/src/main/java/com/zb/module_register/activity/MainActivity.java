package com.zb.module_register.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.BR;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterMainBinding;
import com.zb.module_register.vm.MainViewModel;

@Route(path = RouteUtils.Register_Main)
public class MainActivity extends RegisterBaseActivity {
    private MainViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.register_main;
    }

    @Override
    public void initUI() {
        viewModel = new MainViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
        MineApp.registerInfo.setPhone(PreferenceUtil.readStringValue(activity, "userName"));
        RegisterMainBinding binding = (RegisterMainBinding) mBinding;
        AdapterBinding.viewSize(binding.ivBoy, ObjectUtils.getViewSizeByWidth(300f / 1080f), ObjectUtils.getViewSizeByWidth(300f / 1080f));
        AdapterBinding.viewSize(binding.ivGirl, ObjectUtils.getViewSizeByWidth(300f / 1080f), ObjectUtils.getViewSizeByWidth(300f / 1080f));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
