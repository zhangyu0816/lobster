package com.zb.module_register;

import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterNickBinding;
import com.zb.module_register.vm.NickViewModel;

@Route(path = RouteUtils.Register_Nick)
public class NickNameActivity extends BaseActivity {
    private NickViewModel viewModel;
    private RegisterNickBinding binding;

    @Override
    public int getRes() {
        return R.layout.register_nick;
    }

    @Override
    public void initUI() {
        viewModel = new NickViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        binding = (RegisterNickBinding) mBinding;
        ViewGroup.LayoutParams lp = binding.includeLayout.whiteView.getLayoutParams();
        lp.width = MineApp.W / 6;
        binding.includeLayout.whiteView.setLayoutParams(lp);
    }
}
