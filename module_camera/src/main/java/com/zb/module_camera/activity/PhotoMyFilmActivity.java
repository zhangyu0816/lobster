package com.zb.module_camera.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.PhotoMyFilmViewModel;

@Route(path = RouteUtils.Camera_Photo_My_Film)
public class PhotoMyFilmActivity extends WhiteCameraBaseActivity {
    @Autowired(name = "filmMsgCount")
    int filmMsgCount;

    private PhotoMyFilmViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_photo_my_film;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new PhotoMyFilmViewModel();
        viewModel.filmMsgCount = filmMsgCount;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
