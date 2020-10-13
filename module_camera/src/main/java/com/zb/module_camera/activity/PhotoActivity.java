package com.zb.module_camera.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.PhotoViewModel;

@Route(path = RouteUtils.Camera_Photo)
public class PhotoActivity extends CameraBaseActivity {
    @Autowired(name = "isMore")
    boolean isMore;
    @Autowired(name = "showBottom")
    boolean showBottom;
    @Autowired(name = "showVideo")
    boolean showVideo;

    private PhotoViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.camera_photo;
    }

    @Override
    public void initUI() {
        viewModel = new PhotoViewModel();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null)
                viewModel.back(null);
            return true;
        }
        return false;
    }
}
