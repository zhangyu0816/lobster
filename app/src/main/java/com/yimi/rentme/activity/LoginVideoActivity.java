package com.yimi.rentme.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.igexin.sdk.PushManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.databinding.AcLoginVideoBinding;
import com.yimi.rentme.vm.LoadingViewModel;
import com.yimi.rentme.vm.LoginVideoViewModel;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RomUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.StatusBarUtil;

import androidx.databinding.DataBindingUtil;

public class LoginVideoActivity extends RxAppCompatActivity {

    private AcLoginVideoBinding mBinding;
    private LoginVideoViewModel viewModel;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (RomUtils.isHuawei()) {
            StatusBarUtil.setStatusBarColor(this, R.color.black);
        } else {
            StatusBarUtil.statusBarLightMode(this);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.ac_login_video);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!RomUtils.isHuawei()) {
            fitComprehensiveScreen();
        }
        viewModel = new LoginVideoViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    private void fitComprehensiveScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);// 导致华为手机模糊
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 导致华为手机黑屏
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PushManager.getInstance().turnOffPush(MineApp.instance);
    }

    // 监听程序退出
    private long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                SCToastUtil.showToast(this, "再按一次退出程序", true);
                exitTime = System.currentTimeMillis();
            } else {
                MineApp.getApp().exit();
                System.exit(0);
                PushManager.getInstance().turnOffPush(MineApp.instance);
                mBinding = null;
                viewModel = null;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
