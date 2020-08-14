package com.zb.module_camera.vm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.databinding.CameraVideoPlayBinding;
import com.zb.module_camera.iv.VideoPlayVMInterface;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class VideoPlayViewModel extends BaseViewModel implements VideoPlayVMInterface {
    private CameraVideoPlayBinding mBinding;
    public String filePath = "";
    public boolean isUpload = false;
    public boolean isDelete = false;
    private long duration;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CameraVideoPlayBinding) binding;
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);//让电影循环播放
            duration = mp.getDuration();
            changeVideoSize(mp);
        });
        mBinding.videoView.setVideoPath(filePath);
        mBinding.videoView.start();
    }

    @Override
    public void back(View view) {
        mBinding.videoView.stopPlayback();//停止播放视频,并且释放
        mBinding.videoView.suspend();//在任何状态下释放媒体播放器
        if (isUpload) {
            if (MineApp.isLocation) {
                ActivityUtils.getCameraVideos(MineApp.showBottom);
            } else {
                ActivityUtils.getCameraVideo(MineApp.showBottom);
            }
        }
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        if (isUpload) {
            if (MineApp.toPublish && !MineApp.toContinue) {
                MineApp.cameraType = 1;
                MineApp.isMore = false;
                MineApp.filePath = filePath;
                MineApp.time = duration;
                ActivityUtils.getHomePublishImage();
            } else {
                Intent data = new Intent("lobster_camera");
                data.putExtra("cameraType", 1);
                data.putExtra("filePath", filePath);
                data.putExtra("time", duration);
                activity.sendBroadcast(data);
            }
        }
        if (isDelete)
            activity.sendBroadcast(new Intent("lobster_deleteVideo"));

        mBinding.videoView.stopPlayback();//停止播放视频,并且释放
        mBinding.videoView.suspend();//在任何状态下释放媒体播放器
        activity.finish();
    }

    @Override
    public void play(View view) {
        if (mBinding.videoPlay.getVisibility() == View.VISIBLE) {
            mBinding.videoPlay.setVisibility(View.GONE);
            mBinding.videoView.setVideoPath(filePath);
            mBinding.videoView.start();
        } else {
            mBinding.videoView.pause();
            mBinding.videoPlay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        int width = mMediaPlayer.getVideoWidth();
        int height = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(1.0f) * width / height > MineApp.W) {
            AdapterBinding.viewSize(mBinding.videoView, MineApp.W, (MineApp.W * height / width));
        } else {
            AdapterBinding.viewSize(mBinding.videoView, (ObjectUtils.getViewSizeByHeight(1.0f) * width / height), ObjectUtils.getViewSizeByHeight(1.0f));
        }
    }
}
