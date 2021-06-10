package com.zb.module_camera.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RomUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.PhotoStudioViewModel;

import androidx.databinding.library.baseAdapters.BR;

@Route(path = RouteUtils.Camera_Photo_Studio)
public class PhotoStudioActivity extends CameraBaseActivity {

    private PhotoStudioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (RomUtils.isHuawei()) {
            StatusBarUtil.setStatusBarColor(activity, R.color.black);
        } else {
            StatusBarUtil.transparencyBar(activity);
        }
    }

    @Override
    public int getRes() {
        return R.layout.ac_photo_studio;
    }

    @Override
    public void initUI() {
        if (!RomUtils.isHuawei()) {
            fitComprehensiveScreen();
        }
        viewModel = new PhotoStudioViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.openCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
