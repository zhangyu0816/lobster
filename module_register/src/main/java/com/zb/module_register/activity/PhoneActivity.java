package com.zb.module_register.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.BR;
import com.zb.module_register.R;
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
        fitComprehensiveScreen();
        viewModel = new PhoneViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.isLogin = isLogin;
        RegisterPhoneBinding binding = (RegisterPhoneBinding) mBinding;
        binding.setBtnName("获取验证码");
        binding.setRemark(isLogin ? "" : "您的手机号将获取验证码");
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W / 2, 5);

        // 按钮向上移
        KeyBroadUtils.controlKeyboardLayout(binding.btnLayout, binding.tvNext);
        // 初始化手机号
        if (isLogin) {
            if(MineApp.registerInfo.getPhone().isEmpty()){
                binding.edPhone.setText(PreferenceUtil.readStringValue(activity, "userName"));
                binding.setPhone(PreferenceUtil.readStringValue(activity, "userName"));
            }else{
                binding.edPhone.setText(MineApp.registerInfo.getPhone());
                binding.setPhone(MineApp.registerInfo.getPhone());
            }
        } else {
            binding.edPhone.setText(MineApp.registerInfo.getPhone());
            binding.setPhone(MineApp.registerInfo.getPhone());
        }
        if (!binding.getPhone().isEmpty()) {
            binding.edPhone.setSelection(binding.getPhone().length());
            binding.tvNext.setBackgroundResource(R.drawable.btn_bg_white_radius60);
            binding.tvNext.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_7a4));
        }

        binding.edPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = binding.edPhone.getText().toString();
                if (content.length() >= 11) {
                    binding.tvNext.setBackgroundResource(R.drawable.btn_bg_white_radius60);
                    binding.tvNext.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_7a4));
                } else {
                    binding.tvNext.setBackgroundResource(R.drawable.btn_bg_purple_af9_radius60);
                    binding.tvNext.setTextColor(MineApp.getInstance().getResources().getColor(R.color.purple_cab));
                }
            }
        });
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
