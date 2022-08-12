package com.yimi.rentme.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.databinding.AcLoadingBinding;
import com.yimi.rentme.vm.LoadingViewModel;
import com.zb.lib_base.app.MineApp;

import androidx.databinding.DataBindingUtil;

public class LoadingActivity extends RxAppCompatActivity {
    private LoadingViewModel viewModel;
    private AcLoadingBinding mBinding;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MineApp.getApp().addActivity(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.ac_loading);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewModel = new LoadingViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
