package com.zb.module_camera.vm;

import android.media.MediaPlayer;
import android.view.View;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.databinding.CameraVideoPlayBinding;
import com.zb.module_camera.iv.VideoPlayVMInterface;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class VideoPlayViewModel extends BaseViewModel implements VideoPlayVMInterface {
    private CameraVideoPlayBinding mBinding;
    public String filePath = "";

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (CameraVideoPlayBinding) binding;
        initVideo();
    }

    @Override
    public void back(View view) {
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

    private void initVideo() {
        //视频加载完成,准备好播放视频的回调
        mBinding.videoView.setOnPreparedListener(mp -> {
            mBinding.progress.setVisibility(View.GONE);
            mBinding.videoPlay.setVisibility(View.GONE);
            //尺寸变化回调
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> changeVideoSize(mp1));
        });
        //视频播放完成后的回调
        mBinding.videoView.setOnCompletionListener(mp -> {

            mBinding.videoPlay.setVisibility(View.VISIBLE);
            mBinding.videoView.stopPlayback();//停止播放视频,并且释放
            mBinding.videoView.suspend();//在任何状态下释放媒体播放器
        });
        //异常回调
        mBinding.videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
        });

        //信息回调
        mBinding.videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "视频播放失败", true);
                mBinding.videoPlay.setVisibility(View.VISIBLE);
                mBinding.videoView.stopPlayback();//停止播放视频,并且释放
                mBinding.videoView.suspend();//在任何状态下释放媒体播放器
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // 缓冲开始
                mBinding.progress.setVisibility(View.VISIBLE);
                mBinding.videoPlay.setVisibility(View.GONE);
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                if (mp.isPlaying()) {
                    mBinding.progress.setVisibility(View.GONE);
                    mBinding.videoPlay.setVisibility(View.GONE);
                }
                return true;
            }
//                what 对应返回的值如下
//                public static final int MEDIA_INFO_UNKNOWN = 1;  媒体信息未知
//                public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; 媒体信息视频跟踪滞后
//                public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3; 媒体信息\视频渲染\开始
//                public static final int MEDIA_INFO_BUFFERING_START = 701; 媒体信息缓冲启动
//                public static final int MEDIA_INFO_BUFFERING_END = 702; 媒体信息缓冲结束
//                public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703; 媒体信息网络带宽（703）
//                public static final int MEDIA_INFO_BAD_INTERLEAVING = 800; 媒体-信息-坏-交错
//                public static final int MEDIA_INFO_NOT_SEEKABLE = 801; 媒体信息找不到
//                public static final int MEDIA_INFO_METADATA_UPDATE = 802; 媒体信息元数据更新
//                public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901; 媒体信息不支持字幕
//                public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902; 媒体信息字幕超时
            return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
        });

        mBinding.videoView.setVideoPath(filePath);
        mBinding.videoView.start();
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        int width = mMediaPlayer.getVideoWidth();
        int height = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(0.9f) * width / height > MineApp.W) {
            AdapterBinding.viewSize(mBinding.videoView, MineApp.W, (MineApp.W * height / width));
        } else {
            AdapterBinding.viewSize(mBinding.videoView, (ObjectUtils.getViewSizeByHeight(0.9f) * width / height), ObjectUtils.getViewSizeByHeight(0.9f));
        }
    }
}
