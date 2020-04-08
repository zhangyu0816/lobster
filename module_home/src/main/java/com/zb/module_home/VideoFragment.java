package com.zb.module_home;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.vm.VideoViewModel;

@Route(path = RouteUtils.Home_Video_Fragment)
public class VideoFragment extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.home_video;
    }

    @Override
    public void initUI() {
        VideoViewModel viewModel = new VideoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
