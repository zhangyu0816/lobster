package com.zb.module_mine.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.OpenVipViewModel;

@Route(path = RouteUtils.Mine_Open_Vip)
public class OpenVipActivity extends BaseActivity {
    @Autowired(name = "isFinish")
    boolean isFinish;

    private OpenVipViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
    }

    @Override
    public int getRes() {
        return R.layout.mine_open_vip;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new OpenVipViewModel();
        viewModel.isFinish = isFinish;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.vipInfo, MineApp.vipInfoList.get(0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            viewModel.openVipReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
