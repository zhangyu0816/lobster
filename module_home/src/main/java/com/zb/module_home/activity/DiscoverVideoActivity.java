package com.zb.module_home.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;
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
    }

    @Override
    public int getRes() {
        return R.layout.home_discover_video;
    }

    @Override
    public void initUI() {
        viewModel = new DiscoverVideoViewModel();
        viewModel.friendDynId = friendDynId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            viewModel.videoPlay(null);
        }
        isFirst = false;
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
