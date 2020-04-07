package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterBirthdayBinding;
import com.zb.module_register.vm.BirthdayViewModel;

@Route(path = RouteUtils.Register_Birthday)
public class BirthdayActivity extends RegisterBaseActivity {
    @Override
    public int getRes() {
        return R.layout.register_birthday;
    }

    @Override
    public void initUI() {
        BirthdayViewModel viewModel = new BirthdayViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);

        RegisterBirthdayBinding binding = (RegisterBirthdayBinding) mBinding;
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W / 3, 5);

        binding.setBirthday(MineApp.registerInfo.getBirthday());
        binding.setTitle("嗨 "+MineApp.registerInfo.getName()+"！您的生日是？");
    }
}
