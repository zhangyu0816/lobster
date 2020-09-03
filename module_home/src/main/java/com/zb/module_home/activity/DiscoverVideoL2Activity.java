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
import com.zb.module_home.vm.DiscoverVideoL2ViewModel;

@Route(path = RouteUtils.Home_Discover_Video_L2)
public class DiscoverVideoL2Activity extends BaseActivity {
    @Autowired(name = "friendDynId")
    long friendDynId;

    private DiscoverVideoL2ViewModel viewModel;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeVideoTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarColor(activity, R.color.black);
    }

    @Override
    public int getRes() {
        return R.layout.home_video_l2;
    }

    @Override
    public void initUI() {
        MineApp.activityMap.put("DiscoverVideoL2Activity", new DiscoverVideoL2Activity());
        viewModel = new DiscoverVideoL2ViewModel();
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
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
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
