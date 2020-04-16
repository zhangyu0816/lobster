package com.zb.module_home;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.DiscoverDetailViewModel;

@Route(path = RouteUtils.Home_Discover_Detail)
public class DiscoverDetailActivity extends HomeBaseActivity {

    @Autowired(name = "friendDynId")
    long friendDynId;

    @Override
    public int getRes() {
        return R.layout.home_discover_detail;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        DiscoverDetailViewModel viewModel = new DiscoverDetailViewModel();
        viewModel.friendDynId = friendDynId;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);


    }
}
