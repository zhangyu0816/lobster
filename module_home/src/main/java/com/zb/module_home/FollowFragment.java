package com.zb.module_home;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.FollowViewModel;

@Route(path = RouteUtils.Home_Follow_Fragment)
public class FollowFragment extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.home_follow;
    }

    @Override
    public void initUI() {
        FollowViewModel viewModel = new FollowViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
