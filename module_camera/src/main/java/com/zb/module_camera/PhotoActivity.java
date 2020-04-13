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

    private void fitComprehensiveScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.cancel(null);
            return true;
        }
        return false;
    }
}
