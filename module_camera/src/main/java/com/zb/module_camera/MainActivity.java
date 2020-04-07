package com.zb.module_camera;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.databinding.CameraMainBinding;

@Route(path = RouteUtils.Camera_Main)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CameraTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getRes() {
        return R.layout.camera_main;
    }

    @Override
    public void initUI() {
        CameraViewModel viewModel = new CameraViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);

        CameraMainBinding binding = (CameraMainBinding) mBinding;
        viewModel.selectImage(0);
        AdapterBinding.viewSize(binding.imagesList, MineApp.W, (int) (MineApp.H * 0.4f));

    }
}
