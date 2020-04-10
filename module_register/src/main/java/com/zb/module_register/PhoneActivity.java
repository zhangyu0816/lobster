package com.zb.module_register;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.databinding.RegisterPhoneBinding;
import com.zb.module_register.vm.PhoneViewModel;

@Route(path = RouteUtils.Register_Phone)
public class PhoneActivity extends RegisterBaseActivity {
    @Autowired(name = "isLogin")
    boolean isLogin;

    private PhoneViewModel viewModel;
    @Override
    public int getRes() {
        return R.layout.register_phone;
    }

    @Override
    public void initUI() {
         viewModel = new PhoneViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.isLogin = isLogin;
        RegisterPhoneBinding binding = (RegisterPhoneBinding) mBinding;
        binding.setBtnName(isLogin ? "继续" : "获取验证码");
        binding.setRemark(isLogin ? "" : "您的手机号将获取验证码");
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W / 2, 5);

        // 按钮向上移
        KeyBroadUtils.controlKeyboardLayout(binding.btnLayout, binding.tvNext);
        // 初始化手机号
        binding.setPhone(MineApp.registerInfo.getPhone());
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
