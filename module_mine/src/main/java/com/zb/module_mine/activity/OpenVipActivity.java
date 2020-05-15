package com.zb.module_mine.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.OpenVipViewModel;

@Route(path = RouteUtils.Mine_Open_Vip)
public class OpenVipActivity extends BaseActivity {
    private OpenVipViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.mine_open_vip;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new OpenVipViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.openVipReceiver.unregisterReceiver();
    }
}
