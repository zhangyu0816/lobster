package com.zb.module_camera.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.FilmFragViewModel;

@Route(path = RouteUtils.Camera_Film_Fragment)
public class FilmFrag extends BaseFragment {
    @Override
    public int getRes() {
        return R.layout.frag_film;
    }

    @Override
    public void initUI() {
        FilmFragViewModel viewModel = new FilmFragViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
