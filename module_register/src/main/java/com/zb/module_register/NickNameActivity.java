package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterNickBinding;
import com.zb.module_register.vm.NickViewModel;

@Route(path = RouteUtils.Register_Nick)
public class NickNameActivity extends RegisterBaseActivity {

    @Override
    public int getRes() {
        return R.layout.register_nick;
    }

    @Override
    public void initUI() {
        NickViewModel viewModel = new NickViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        RegisterNickBinding binding = (RegisterNickBinding) mBinding;

        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W / 6, 5);

        // 按钮向上移
        KeyBroadUtils.controlKeyboardLayout(binding.btnLayout, binding.tvNext);
        // 初始化昵称
        binding.setNick(MineApp.registerInfo.getName());
    }


}
