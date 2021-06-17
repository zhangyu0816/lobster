package com.zb.module_camera.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.PhotoMyFilmViewModel;

@Route(path = RouteUtils.Camera_Photo_My_Film)
public class PhotoMyFilmActivity extends WhiteCameraBaseActivity {

    @Override
    public int getRes() {
        return R.layout.ac_photo_my_film;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        PhotoMyFilmViewModel viewModel = new PhotoMyFilmViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
