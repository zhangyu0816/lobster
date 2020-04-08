package com.zb.module_home;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.RecommendViewModel;

@Route(path = RouteUtils.Home_Recommend_Fragment)
public class RecommendFragment extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.home_recommend;
    }

    @Override
    public void initUI() {
        RecommendViewModel viewModel = new RecommendViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
