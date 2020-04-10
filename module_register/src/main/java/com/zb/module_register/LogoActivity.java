package com.zb.module_register;

import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterLogoBinding;
import com.zb.module_register.vm.LogoViewModel;

@Route(path = RouteUtils.Register_Logo)
public class LogoActivity extends RegisterBaseActivity {
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

        RegisterLogoBinding binding = (RegisterLogoBinding) mBinding;
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W * 5 / 6, 5);

        // 上传头像
        AdapterBinding.viewSize(binding.uploadRelative, ObjectUtils.getViewSizeByWidth(0.4f), ObjectUtils.getLogoHeight(0.4f));

        mBinding.setVariable(BR.imageUrl, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            String fileName = data.getStringExtra("fileName");
            mBinding.setVariable(BR.imageUrl, fileName);
        }
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
