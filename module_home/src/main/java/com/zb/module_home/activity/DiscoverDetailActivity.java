package com.zb.module_home.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SoftHideKeyBoardUtil;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.DiscoverDetailViewModel;

import java.util.Objects;

@Route(path = RouteUtils.Home_Discover_Detail)
public class DiscoverDetailActivity extends BaseActivity {

    @Autowired(name = "friendDynId")
    long friendDynId;

    private DiscoverDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(activity);
    }

    @Override
    public int getRes() {
        return R.layout.home_discover_detail;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();

        MineApp.getApp().getActivityMap().put("DiscoverDetailActivity", activity);
        viewModel = new DiscoverDetailViewModel();

        Uri uri = getIntent().getData();
        if (friendDynId == 0 && uri != null) {
            friendDynId = Long.parseLong(Objects.requireNonNull(uri.getQueryParameter("friendDynId")));
        }

        viewModel.friendDynId = friendDynId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        SoftHideKeyBoardUtil.assistActivity(activity, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null){
                viewModel.back(null);
                mBinding = null;
                viewModel = null;
            }
            return true;
        }
        return false;
    }
}
