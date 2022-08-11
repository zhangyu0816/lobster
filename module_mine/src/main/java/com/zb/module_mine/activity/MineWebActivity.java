package com.zb.module_mine.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MineWebBinding;
import com.zb.module_mine.vm.MineWebViewModel;

import androidx.databinding.DataBindingUtil;

public class MineWebActivity extends RxAppCompatActivity {

    private MineWebBinding mBinding;
    private MineWebViewModel viewModel;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MineTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.mine_web);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewModel = new MineWebViewModel();
        viewModel.url = getIntent().getStringExtra("url");
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, getIntent().getStringExtra("title"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        mBinding = null;
        viewModel = null;
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
