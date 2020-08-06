package com.zb.module_camera.vm;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.VideoInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.iv.VideosVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;


public class VideosViewModel extends BaseViewModel implements VideosVMInterface {
    public boolean showBottom;
    public CameraAdapter adapter;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private VideoInfo videoInfo;
    private Thread mThread;
    private boolean mWorking = true;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
        getVideoFile(Environment.getExternalStorageDirectory());
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_camera_video, videoInfoList, this);
    }

    @Override
    public void back(View view) {
        ActivityUtils.getCameraVideo(showBottom);
        activity.finish();
    }

    @Override
    public void selectVideo(int position) {
        mThread.interrupt();
        adapter.setSelectIndex(position);
        videoInfo = videoInfoList.get(position);
        MineApp.isLocation = true;
        MineApp.showBottom = showBottom;
        ActivityUtils.getCameraVideoPlay(videoInfo.getPath(), true, false);
        activity.finish();
    }

    private int start = 0;

    private void getVideoFile(File file) {// 获得视频文件
        mThread = new Thread(() -> file.listFiles(file1 -> {
            if (mWorking) {
                // sdCard找到视频名称
                String name = file1.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")) {
                        start = videoInfoList.size();
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
                adapter.notifyItemRangeChanged(start, videoInfoList.size());
            }
            return false;
        }
    });
}
