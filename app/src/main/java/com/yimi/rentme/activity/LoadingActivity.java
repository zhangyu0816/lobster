package com.yimi.rentme.activity;

import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.LoadingViewModel;
import com.zb.lib_base.utils.PreferenceUtil;

public class LoadingActivity extends AppBaseActivity {
    @Override
    public int getRes() {
        return R.layout.ac_loading;
    }

    @Override
    public void initUI() {
        PreferenceUtil.saveIntValue(activity, "loginType", 1);
        LoadingViewModel viewModel = new LoadingViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.myInfo();
    }
}
