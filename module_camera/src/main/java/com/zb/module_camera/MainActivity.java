package com.zb.module_camera;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.databinding.CameraMainBinding;
import com.zb.module_camera.vm.CameraViewModel;

@Route(path = RouteUtils.Camera_Main)
public class MainActivity extends CameraBaseActivity {

    @Autowired(name = "isMore")
    boolean isMore;

    @Override
    public int getRes() {
        return R.layout.camera_main;
    }

    @Override
    public void initUI() {
        CameraViewModel viewModel = new CameraViewModel();
        viewModel.isMore = isMore;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.isMore, isMore);
        AdapterBinding.viewSize(((CameraMainBinding) mBinding).imagesList, MineApp.W, (int) (MineApp.H * 0.4f));

    }
}
