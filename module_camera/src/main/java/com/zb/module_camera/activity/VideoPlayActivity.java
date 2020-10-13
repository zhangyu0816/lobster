package com.zb.module_camera.activity;

import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_camera.BR;
import com.zb.module_camera.R;
import com.zb.module_camera.vm.VideoPlayViewModel;

@Route(path = RouteUtils.Camera_Video_Play)
public class VideoPlayActivity extends CameraBaseActivity {
    private VideoPlayViewModel viewModel;

    @Autowired(name = "filePath")
    String filePath = "";
    @Autowired(name = "isUpload")
    boolean isUpload;
    @Autowired(name = "isDelete")
    boolean isDelete;

    @Override
    public int getRes() {
        return R.layout.camera_video_play;
    }

    @Override
    public void initUI() {
        viewModel = new VideoPlayViewModel();
        viewModel.filePath = filePath;
        viewModel.isUpload = isUpload;
        viewModel.isDelete = isDelete;
        mBinding.setVariable(BR.viewModel, viewModel);
        mBinding.setVariable(BR.isUpload, isUpload);
        mBinding.setVariable(BR.isDelete, isDelete);
        viewModel.setBinding(mBinding);
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
