package com.zb.module_camera.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.databinding.CameraVideosBinding;
import com.zb.module_camera.vm.VideosViewModel;

@Route(path = RouteUtils.Camera_Videos)
public class VideosActivity extends CameraBaseActivity {
    @Autowired(name = "showBottom")
    boolean showBottom;

    private VideosViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.camera_videos;
    }

    @Override
    public void initUI() {
        viewModel = new VideosViewModel();
        viewModel.showBottom = showBottom;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        AdapterBinding.viewSize(((CameraVideosBinding) mBinding).imagesList, MineApp.W, (int) (MineApp.H * 0.4f));
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
