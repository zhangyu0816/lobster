package com.yimi.rentme.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MineApp.getApp().addActivity(this);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.ac_loading);

        ARouter.getInstance().inject(this);
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
        viewModel.onDestroy();
    }
}
