package com.zb.module_camera.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.PhotoWallViewModel;

@Route(path = RouteUtils.Camera_Photo_Wall)
public class PhotoWallActivity extends WhiteCameraBaseActivity {
    @Autowired(name = "surplusCount")
    int surplusCount;
    @Autowired(name = "cameraFilmId")
    long cameraFilmId;

    @Override
    public int getRes() {
        return R.layout.ac_photo_wall;
    }

    @Override
    public void initUI() {
        PhotoWallViewModel viewModel = new PhotoWallViewModel();
        viewModel.surplusCount = surplusCount;
        viewModel.cameraFilmId = cameraFilmId;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }
}
