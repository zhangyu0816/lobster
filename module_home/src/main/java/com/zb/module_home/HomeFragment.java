package com.zb.module_home;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.HomeViewModel;

@Route(path = RouteUtils.Home_Fragment)
public class HomeFragment extends BaseFragment {

    @Override
    public int getRes() {
        return R.layout.home_frag;
    }

    @Override
    public void initUI() {
        HomeViewModel viewModel = new HomeViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

    }
}
