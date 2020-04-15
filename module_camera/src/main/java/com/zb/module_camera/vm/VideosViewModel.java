package com.zb.module_camera.vm;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zb.lib_base.model.VideoInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.BR;
import com.zb.module_camera.FileModel;
import com.zb.module_camera.R;
import com.zb.module_camera.adapter.CameraAdapter;
import com.zb.module_camera.databinding.CameraVideosBinding;
import com.zb.module_camera.iv.VideosVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class VideosViewModel extends BaseViewModel implements VideosVMInterface {

    private CameraVideosBinding videosBinding;
    public CameraAdapter adapter;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private List<FileModel> fileList = new ArrayList<>();
    private Map<String, List<String>> fileMap = new HashMap<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        videosBinding = (CameraVideosBinding) binding;
        setAdapter();
        getVideoFile(Environment.getExternalStorageDirectory());
    }

    @Override
    public void setAdapter() {
        adapter = new CameraAdapter<>(activity, R.layout.item_videos, videoInfoList, this);
    }

    @Override
    public void back(View view) {
        ActivityUtils.getCameraVideo();
        activity.finish();
    }

    @Override
    public void upload(View view) {

    }

    @Override
    public void selectVideo(int position) {
        adapter.setSelectIndex(position);
    }

    private void getVideoFile(File file) {// 获得视频文件
        new Thread(() -> file.listFiles(file1 -> {
            // sdCard找到视频名称
            String name = file1.getName();
            int i = name.indexOf('.');
            if (i != -1) {
                name = name.substring(i);
                if (name.equalsIgnoreCase(".mp4")
                        || name.equalsIgnoreCase(".3gp")
                        || name.equalsIgnoreCase(".wmv")
                        || name.equalsIgnoreCase(".ts")
                        || name.equalsIgnoreCase(".rmvb")
                        || name.equalsIgnoreCase(".mov")
                        || name.equalsIgnoreCase(".m4v")
                        || name.equalsIgnoreCase(".avi")
                        || name.equalsIgnoreCase(".m3u8")
                        || name.equalsIgnoreCase(".3gpp")
                        || name.equalsIgnoreCase(".3gpp2")
                        || name.equalsIgnoreCase(".mkv")
                        || name.equalsIgnoreCase(".flv")
                        || name.equalsIgnoreCase(".divx")
                        || name.equalsIgnoreCase(".f4v")
                        || name.equalsIgnoreCase(".rm")
                        || name.equalsIgnoreCase(".asf")
                        || name.equalsIgnoreCase(".ram")
                        || name.equalsIgnoreCase(".mpg")
                        || name.equalsIgnoreCase(".v8")
                        || name.equalsIgnoreCase(".swf")
                        || name.equalsIgnoreCase(".m2v")
                        || name.equalsIgnoreCase(".asx")
                        || name.equalsIgnoreCase(".ra")
                        || name.equalsIgnoreCase(".ndivx")
                        || name.equalsIgnoreCase(".xvid")) {
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
            return false;
        })).start();
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
