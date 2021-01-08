package com.zb.module_home.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.DiscoverVideoViewModel;

@Route(path = RouteUtils.Home_Discover_Video)
public class DiscoverVideoActivity extends BaseActivity {
    @Autowired(name = "friendDynId")
    long friendDynId;

    private DiscoverVideoViewModel viewModel;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeVideoTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarColor(activity, R.color.black);
    }

    @Override
    public int getRes() {
        return R.layout.home_video;
    }

    @Override
    public void initUI() {
        MineApp.getApp().getActivityMap().put("DiscoverVideoActivity", activity);
        viewModel = new DiscoverVideoViewModel();
        viewModel.friendDynId = friendDynId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst && viewModel != null) {
            viewModel.videoPlay(null);
        }
        isFirst = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null)
                viewModel.back(null);
            return true;
        }
        return false;
    }
}
