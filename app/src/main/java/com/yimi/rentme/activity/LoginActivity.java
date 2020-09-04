package com.yimi.rentme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.LoginViewModel;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;

@Route(path = RouteUtils.Main_Login)
public class LoginActivity extends BaseActivity {

    @Autowired(name = "loginStep")
    int loginStep;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.RegisterTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.ac_login;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new LoginViewModel();
        viewModel.loginStep = loginStep;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            viewModel.setSingleLogo(data.getStringExtra("fileName"));
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            activity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
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