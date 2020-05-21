package com.zb.module_home.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.ReportViewModel;

@Route(path = RouteUtils.Home_Report)
public class ReportActivity extends HomeBaseActivity {
    @Override
    public int getRes() {
        return R.layout.home_report;
    }

    @Override
    public void initUI() {
        ReportViewModel viewModel = new ReportViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }
}
