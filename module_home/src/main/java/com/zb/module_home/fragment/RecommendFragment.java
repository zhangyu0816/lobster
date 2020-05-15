package com.zb.module_home.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.RecommendViewModel;

@Route(path = RouteUtils.Home_Recommend_Fragment)
public class RecommendFragment extends BaseFragment {
    private RecommendViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_recommend;
    }

    @Override
    public void initUI() {
        viewModel = new RecommendViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
