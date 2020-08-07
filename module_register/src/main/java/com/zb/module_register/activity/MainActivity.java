package com.zb.module_register.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.module_register.BR;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterMainBinding;
import com.zb.module_register.vm.MainViewModel;

import androidx.annotation.Nullable;

@Route(path = RouteUtils.Register_Main)
public class MainActivity extends RegisterBaseActivity {

    private long exitTime = 0;
    private MainViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.register_main;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new MainViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
        RegisterMainBinding binding = (RegisterMainBinding) mBinding;
        AdapterBinding.viewSize(binding.ivBoy, ObjectUtils.getViewSizeByWidth(300f / 1080f), ObjectUtils.getViewSizeByWidth(300f / 1080f));
        AdapterBinding.viewSize(binding.ivGirl, ObjectUtils.getViewSizeByWidth(300f / 1080f), ObjectUtils.getViewSizeByWidth(300f / 1080f));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!MineApp.isLogin) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    SCToastUtil.showToast(activity, "再按一次退出程序", false);
                    exitTime = System.currentTimeMillis();
                } else {
                    System.exit(0);
                }
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            assert data != null;
            viewModel.registerByUnion(data.getStringExtra("userName"), data.getStringExtra("captcha"));
        }
    }
}
