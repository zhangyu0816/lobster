package com.zb.module_home.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_home.R;
import com.zb.module_home.vm.DiscoverDetailViewModel;

@Route(path = RouteUtils.Home_Discover_Detail)
public class DiscoverDetailActivity extends BaseActivity {

    @Autowired(name = "friendDynId")
    long friendDynId;

    private DiscoverDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightModeNotFull(this);
    }

    @Override
    public int getRes() {
        return R.layout.home_discover_detail;
    }

    @Override
    public void initUI() {
        viewModel = new DiscoverDetailViewModel();
        viewModel.friendDynId = friendDynId;
        viewModel.setBinding(mBinding);
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
