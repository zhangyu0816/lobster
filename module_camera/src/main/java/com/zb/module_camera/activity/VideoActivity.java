package com.zb.module_camera.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.VideoViewModel;

@Route(path = RouteUtils.Camera_Video)
public class VideoActivity extends CameraBaseActivity {
    @Autowired(name = "showBottom")
    boolean showBottom;
    private VideoViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.camera_video;
    }

    @Override
    public void initUI() {
        viewModel = new VideoViewModel();
        viewModel.showBottom = showBottom;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.showBottom, showBottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.onDestroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
