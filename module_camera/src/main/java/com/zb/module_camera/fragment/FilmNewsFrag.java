package com.zb.module_camera.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.FilmFragViewModel;
import com.zb.module_camera.vm.FilmNewsFragViewModel;

@Route(path = RouteUtils.Camera_Film_News_Fragment)
public class FilmNewsFrag extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.frag_film_news;
    }

    @Override
    public void initUI() {
        FilmNewsFragViewModel viewModel = new FilmNewsFragViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
