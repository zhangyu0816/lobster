package com.zb.module_camera;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.vm.VideoViewModel;

@Route(path = RouteUtils.Camera_Video)
public class VideoActivity extends CameraBaseActivity {

    @Override
    public int getRes() {
        return R.layout.camera_video;
    }

    @Override
    public void initUI() {
        VideoViewModel viewModel = new VideoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

    }
}
