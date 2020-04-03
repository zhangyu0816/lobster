package com.zb.module_register;

import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterLogoBinding;
import com.zb.module_register.vm.LogoViewModel;

@Route(path = RouteUtils.Register_Logo)
public class LogoActivity extends BaseActivity {

    private LogoViewModel viewModel;
    private RegisterLogoBinding binding;

    @Override
    public int getRes() {
        return R.layout.register_logo;
    }

    @Override
    public void initUI() {
        viewModel = new LogoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        binding = (RegisterLogoBinding) mBinding;

        ViewGroup.LayoutParams lp = binding.includeLayout.whiteView.getLayoutParams();
        lp.width = MineApp.W * 5 / 6;
        binding.includeLayout.whiteView.setLayoutParams(lp);

        ViewGroup.LayoutParams lpur = binding.uploadRelative.getLayoutParams();
        lpur.height=  lpur.width = (int) (MineApp.W * 600f/ 1080f);
        binding.uploadRelative.setLayoutParams(lpur);

    }
}
