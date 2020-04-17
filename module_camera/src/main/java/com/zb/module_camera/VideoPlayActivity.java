package com.zb.module_camera;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.vm.VideoPlayViewModel;

@Route(path = RouteUtils.Camera_Video_Play)
public class VideoPlayActivity extends CameraBaseActivity {
    private VideoPlayViewModel viewModel;

    @Autowired(name = "filePath")
    String filePath;

    @Override
    public int getRes() {
        return R.layout.camera_video_play;
    }

    @Override
    public void initUI() {
        viewModel = new VideoPlayViewModel();
        viewModel.filePath = filePath;
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
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