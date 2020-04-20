package com.zb.module_bottle.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.vm.BottleViewModel;

@Route(path = RouteUtils.Bottle_Main)
public class MainActivity extends BottleBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
    }

    @Override
    public int getRes() {
        return R.layout.bottle_main;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        BottleViewModel viewModel = new BottleViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.isOutLine, false);
    }
}
