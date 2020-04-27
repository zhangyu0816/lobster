package com.zb.module_mine.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.vm.SettingViewModel;

@Route(path = RouteUtils.Mine_Setting)
public class SettingActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_setting;
    }

    @Override
    public void initUI() {
        SettingViewModel viewModel = new SettingViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
        mBinding.setVariable(BR.title,"设置");
    }
}
