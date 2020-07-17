package com.zb.module_mine.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.SettingViewModel;

import java.io.File;

@Route(path = RouteUtils.Mine_Setting)
public class SettingActivity extends MineBaseActivity {
    private SettingViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.mine_setting;
    }

    @Override
    public void initUI() {
        viewModel = new SettingViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.title, "设置");
        mBinding.setVariable(BR.cacheSize, DataCleanManager.getCacheSize(new File(String.valueOf(activity.getCacheDir()))));
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

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.humanFaceStatus();
    }
}
