package com.zb.module_camera.vm;

import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.VideoInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.iv.VideosVMInterface;

import androidx.databinding.ViewDataBinding;


public class VideosViewModel extends BaseViewModel implements VideosVMInterface {
    public boolean showBottom;
    public CameraAdapter adapter;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera_video, MineApp.videoInfoList, this);
    }

    @Override
    public void back(View view) {
        ActivityUtils.getCameraVideo(showBottom);
        activity.finish();
    }

    @Override
    public void selectVideo(int position) {
        VideoInfo videoInfo = MineApp.videoInfoList.get(position);
        MineApp.isLocation = true;
        MineApp.showBottom = showBottom;
        ActivityUtils.getCameraVideoPlay(videoInfo.getPath(), true, false);
        activity.finish();
    }
}
