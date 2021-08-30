package com.zb.module_camera.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.CameraViewModel;

@Route(path = RouteUtils.Camera_Main)
public class CameraActivity extends CameraBaseActivity {

    @Autowired(name = "isMore")
    boolean isMore;
    @Autowired(name = "showBottom")
    boolean showBottom;
    @Autowired(name = "showVideo")
    boolean showVideo;

    private CameraViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.camera_main;
    }

    @Override
    public void initUI() {
        viewModel = new CameraViewModel();
        viewModel.isMore = isMore;
        viewModel.showBottom = showBottom;
        viewModel.showVideo = showVideo;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.isMore, isMore);
        mBinding.setVariable(BR.showBottom, showBottom);
        mBinding.setVariable(BR.showVideo, showVideo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
        viewModel = null;
    }
}
