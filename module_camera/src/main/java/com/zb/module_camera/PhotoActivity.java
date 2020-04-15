package com.zb.module_camera;

import android.graphics.Color;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.vm.PhotoViewModel;

@Route(path = RouteUtils.Camera_Photo)
public class PhotoActivity extends CameraBaseActivity {
    private PhotoViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.camera_photo;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new PhotoViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
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
