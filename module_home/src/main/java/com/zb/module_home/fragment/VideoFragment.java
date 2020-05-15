package com.zb.module_home.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.vm.VideoViewModel;

@Route(path = RouteUtils.Home_Video_Fragment)
public class VideoFragment extends BaseFragment {
    private VideoViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_video;
    }

    @Override
    public void initUI() {
        viewModel = new VideoViewModel();
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
