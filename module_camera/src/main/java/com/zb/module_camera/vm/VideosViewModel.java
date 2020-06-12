package com.zb.module_camera.vm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.VideoInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.CameraVideosBinding;
import com.zb.module_camera.iv.VideosVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;


public class VideosViewModel extends BaseViewModel implements VideosVMInterface {

    private CameraVideosBinding videosBinding;
    public CameraAdapter adapter;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private VideoInfo videoInfo;
    private Thread mThread;
    private boolean mWorking = true;
    private int duration = 0;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        videosBinding = (CameraVideosBinding) binding;
        setAdapter();
        getVideoFile(Environment.getExternalStorageDirectory());
        initVideo();
    }

    private void initVideo() {
        //视频加载完成,准备好播放视频的回调
        videosBinding.videoView.setOnPreparedListener(mp -> {
            duration = mp.getDuration();
            videosBinding.progress.setVisibility(View.GONE);
            videosBinding.videoPlay.setVisibility(View.GONE);
            //尺寸变化回调
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> changeVideoSize(mp1));
        });
        //视频播放完成后的回调
        videosBinding.videoView.setOnCompletionListener(mp -> {

            videosBinding.videoPlay.setVisibility(View.VISIBLE);
            videosBinding.videoView.stopPlayback();//停止播放视频,并且释放
            videosBinding.videoView.suspend();//在任何状态下释放媒体播放器
        });
        //异常回调
        videosBinding.videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
        });

        //信息回调
        videosBinding.videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "视频播放失败", true);
                videosBinding.videoPlay.setVisibility(View.VISIBLE);
                videosBinding.videoView.stopPlayback();//停止播放视频,并且释放
                videosBinding.videoView.suspend();//在任何状态下释放媒体播放器
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // 缓冲开始
                videosBinding.progress.setVisibility(View.VISIBLE);
                videosBinding.videoPlay.setVisibility(View.GONE);
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                if (mp.isPlaying()) {
                    videosBinding.progress.setVisibility(View.GONE);
                    videosBinding.videoPlay.setVisibility(View.GONE);
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
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        int width = mMediaPlayer.getVideoWidth();
        int height = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(0.6f) * width / height > MineApp.W) {
            AdapterBinding.viewSize(videosBinding.videoView, MineApp.W, (MineApp.W * height / width));
        } else {
            AdapterBinding.viewSize(videosBinding.videoView, (ObjectUtils.getViewSizeByHeight(0.6f) * width / height), ObjectUtils.getViewSizeByHeight(0.6f));
        }
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera_video, videoInfoList, this);
    }

    @Override
    public void back(View view) {
        if (mWorking) {
            if (mThread != null && mThread.isAlive()) {
                mThread.interrupt();
                mThread = null;
            }
            mWorking = false;
        }
        videosBinding.videoView.stopPlayback();//停止播放视频,并且释放
        videosBinding.videoView.suspend();//在任何状态下释放媒体播放器
        ActivityUtils.getCameraVideo();
        activity.finish();
    }

    @Override
    public void upload(View view) {
        videosBinding.videoView.stopPlayback();//停止播放视频,并且释放
        videosBinding.videoView.suspend();//在任何状态下释放媒体播放器
        Intent data = new Intent("lobster_camera");
        data.putExtra("cameraType", 1);
        data.putExtra("filePath", videoInfo.getPath());
        data.putExtra("time", (long) duration);
        activity.sendBroadcast(data);
        activity.finish();
    }

    @Override
    public void selectVideo(int position) {
        adapter.setSelectIndex(position);
        videoInfo = videoInfoList.get(position);
        videosBinding.videoView.setVideoPath(videoInfo.getPath());
        videosBinding.videoView.start();
    }

    @Override
    public void play(View view) {
        if (videosBinding.videoPlay.getVisibility() == View.VISIBLE) {
            videosBinding.videoPlay.setVisibility(View.GONE);
            videosBinding.videoView.setVideoPath(videoInfo.getPath());
            videosBinding.videoView.start();
        } else {
            videosBinding.videoView.pause();
            videosBinding.videoPlay.setVisibility(View.VISIBLE);
        }
    }

    private void getVideoFile(File file) {// 获得视频文件
        mThread = new Thread(() -> file.listFiles(file1 -> {
            if (mWorking) {
                // sdCard找到视频名称
                String name = file1.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")) {
                        VideoInfo vi = new VideoInfo();
                        vi.setName(file1.getName());
                        vi.setPath(file1.getAbsolutePath());
                        videoInfoList.add(vi);
                        mHandler.sendEmptyMessage(0);

                        return true;
                    }
                } else if (file1.isDirectory()) {
                    getVideoFile(file1);
                }
            }
            return false;
        }));
        mThread.start();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    });
}
