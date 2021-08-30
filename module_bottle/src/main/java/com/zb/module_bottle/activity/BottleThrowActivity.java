package com.zb.module_bottle.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleThrowViewModel;

@Route(path = RouteUtils.Bottle_Throw)
public class BottleThrowActivity extends BaseActivity {

    private BottleThrowViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BottleTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
    }

    @Override
    public int getRes() {
        return R.layout.bottle_throw;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new BottleThrowViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "我的漂流瓶");
        mBinding.setVariable(BR.noReadNum, MineApp.noReadBottleNum);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null){
            viewModel.onDestroy();
            mBinding = null;
            viewModel = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null)
            viewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel != null)
            viewModel.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (viewModel != null)
            viewModel.onResume();
    }
}
