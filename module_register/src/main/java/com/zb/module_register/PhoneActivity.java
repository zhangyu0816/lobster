package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterPhoneBinding;
import com.zb.module_register.vm.PhoneViewModel;

@Route(path = RouteUtils.Register_Phone)
public class PhoneActivity extends RegisterBaseActivity {

    @Override
    public int getRes() {
        return R.layout.register_phone;
    }

    @Override
    public void initUI() {
        PhoneViewModel viewModel = new PhoneViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        RegisterPhoneBinding binding = (RegisterPhoneBinding) mBinding;
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W / 2, 5);

        // 按钮向上移
        KeyBroadUtils.controlKeyboardLayout(binding.btnLayout, binding.tvNext);
        // 初始化昵称
        binding.setPhone(MineApp.registerInfo.getPhone());
    }
}
