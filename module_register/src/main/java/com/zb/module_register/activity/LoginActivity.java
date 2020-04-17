package com.zb.module_register.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.KeyBroadUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_register.BR;
import com.zb.module_register.R;
import com.zb.module_register.databinding.RegisterLoginBinding;
import com.zb.module_register.vm.LoginViewModel;

@Route(path = RouteUtils.Register_Login)
public class LoginActivity extends RegisterBaseActivity {
    private LoginViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.register_login;
    }

    @Override
    public void initUI() {
        viewModel = new LoginViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        RegisterLoginBinding binding = (RegisterLoginBinding) mBinding;
        binding.setRight("验证码登录");
        // 步骤进度跳
        AdapterBinding.viewSize(binding.includeLayout.whiteBg, MineApp.W, 5);
        AdapterBinding.viewSize(binding.includeLayout.whiteView, MineApp.W, 5);

        // 按钮向上移
        KeyBroadUtils.controlKeyboardLayout(binding.btnLayout, binding.tvNext);

        binding.edPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = binding.edPass.getText().toString();
                if (content.length() >= 6) {
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
