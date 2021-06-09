package com.yimi.rentme.activity;

import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.LoadingViewModel;

public class LoadingActivity extends AppBaseActivity {
    private LoadingViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_loading;
    }

    @Override
    public void initUI() {
        viewModel = new LoadingViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
