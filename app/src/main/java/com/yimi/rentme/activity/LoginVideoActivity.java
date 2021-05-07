package com.yimi.rentme.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.LoginVideoViewModel;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RomUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.StatusBarUtil;

@Route(path = RouteUtils.Main_Login_Video)
public class LoginVideoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (RomUtils.isHuawei()) {
            StatusBarUtil.setStatusBarColor(activity, R.color.black);
        } else {
            StatusBarUtil.statusBarLightMode(this);
        }
    }

    @Override
    public int getRes() {
        return R.layout.ac_login_video;
    }

    @Override
    public void initUI() {
        if (!RomUtils.isHuawei()) {
            fitComprehensiveScreen();
        }
        LoginVideoViewModel viewModel = new LoginVideoViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    // 监听程序退出
    private long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                SCToastUtil.showToast(activity, "再按一次退出程序", true);
                exitTime = System.currentTimeMillis();
            } else {
                MineApp.getApp().exit();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
