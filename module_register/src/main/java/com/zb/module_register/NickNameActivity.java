package com.zb.module_register;

import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterNickBinding;
import com.zb.module_register.vm.NickViewModel;

@Route(path = RouteUtils.Register_Nick)
public class NickNameActivity extends BaseActivity {
    private NickViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.register_nick;
    }

    @Override
    public void initUI() {
        viewModel = new NickViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        RegisterNickBinding binding = (RegisterNickBinding) mBinding;
        ViewGroup.LayoutParams lp = binding.includeLayout.whiteView.getLayoutParams();
        lp.width = MineApp.W / 6;
        binding.includeLayout.whiteView.setLayoutParams(lp);
        // 按钮向上移
        KeyBroadUtils.controlKeyboardLayout(binding.btnLayout, binding.tvNext);
        // 初始化昵称
        binding.setNick(MineApp.registerInfo.getName());
    }


}
