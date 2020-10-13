package com.zb.module_home.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.FollowViewModel;

@Route(path = RouteUtils.Home_Follow_Fragment)
public class FollowFragment extends BaseFragment {
    private FollowViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_follow;
    }

    @Override
    public void initUI() {
        viewModel = new FollowViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }
}
