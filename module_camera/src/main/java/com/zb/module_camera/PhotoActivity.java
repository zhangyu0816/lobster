package com.zb.module_camera;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.vm.PhotoViewModel;

@Route(path = RouteUtils.Camera_Photo)
public class PhotoActivity extends CameraBaseActivity {
    @Override
    public int getRes() {
        return R.layout.camera_photo;
    }

    @Override
    public void initUI() {
        PhotoViewModel viewModel = new PhotoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

    }
}
