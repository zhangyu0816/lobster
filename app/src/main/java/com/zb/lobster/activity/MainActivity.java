package com.zb.lobster.activity;

import com.zb.lobster.BR;
import com.zb.lobster.R;
import com.zb.lobster.vm.MainViewModel;

public class MainActivity extends AppBaseActivity {

    @Override
    public int getRes() {
        return R.layout.ac_main;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        MainViewModel viewModel = new MainViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
