package com.zb.module_camera.vm;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.VideoInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.BR;
import com.zb.module_camera.databinding.CameraVideoBinding;
import com.zb.module_camera.iv.VideoVMInterface;
import com.zb.module_camera.utils.CameraPreview;
import com.zb.module_camera.utils.OverCameraView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class VideoViewModel extends BaseViewModel implements VideoVMInterface, View.OnTouchListener {
    private CameraVideoBinding videoBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private boolean isFoucing = false; // 是否正在聚焦
    private OverCameraView mOverCameraView; // 聚焦视图
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private int cameraPosition = 1;// 前后置摄像头
    private int _position = Camera.CameraInfo.CAMERA_FACING_BACK;
    private float x = 16f, y = 9f;

    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private MediaRecorder mRecorder;//音视频录制类
    private long time = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time += 100;
            int s = (int) (time / 1000);
            int ms = (int) (time - s * 1000) / 10;
            videoBinding.setSecond("已录制" + (s < 10 ? "0" + s : s + "") + ":" + (ms < 10 ? "0" + ms : ms + "") + "S");
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        videoBinding = (CameraVideoBinding) binding;
        mBinding.setVariable(BR.sizeIndex, 0);
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, false);
        videoBinding.setVideoPath("");
        videoBinding.cameraLayout.setOnTouchListener(this);
        AdapterBinding.viewSize(videoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * x / y));
        initCamera();
        videoInfoList.clear();
        getVideoFile(Environment.getExternalStorageDirectory());
    }

    private void initCamera() {
        videoBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(_position);
        mRecorder = new MediaRecorder();
        preview = new CameraPreview(activity, mCamera, mRecorder, (int) x, (int) y);
        mOverCameraView = new OverCameraView(activity);
        videoBinding.cameraLayout.addView(preview);
        videoBinding.cameraLayout.addView(mOverCameraView);
    }

    @Override
    public void back(View view) {
        if (videoBinding.getIsRecorder() || videoBinding.getIsFinish()) {
            reset(view);
        } else {
            DataCleanManager.deleteFile(new File(preview.videoPath));
            preview.releaseCamera();
            preview.releaseMediaRecorder();
            activity.finish();
        }
    }

    @Override
    public void reset(View view) {
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, false);
        DataCleanManager.deleteFile(new File(preview.videoPath));
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        initCamera();
    }

    @Override
    public void upload(View view) {
        Intent data = new Intent("lobster_camera");
        data.putExtra("cameraType", 1);
        data.putExtra("filePath", preview.videoPath);
        data.putExtra("time", time);
        activity.sendBroadcast(data);
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        activity.finish();
    }

    @Override
    public void changeSizeIndex(int index) {
        mBinding.setVariable(BR.sizeIndex, index);
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        if (index == 0) {
            x = 16f;
            y = 9f;
        } else {
            x = 4f;
            y = 3f;
        }
        AdapterBinding.viewSize(videoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * x / y));
        initCamera();
    }

    @Override
    public void changeCameraId(View view) {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraCount >= 2 && cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    openCamera(i);
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    openCamera(i);
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    private void openCamera(int position) {
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        _position = position;
        initCamera();
    }

    @Override
    public void createRecorder(View view) {
        if (videoBinding.getIsFinish()) return;
        mBinding.setVariable(BR.isRecorder, true);
        mRecorder.setOnInfoListener((mr, what, extra) -> {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                stopRecorder(view);
                SCToastUtil.showToastBlack(activity, "视频文件已达到3M，自动停止！");
            } else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                stopRecorder(view);
                SCToastUtil.showToastBlack(activity, "视频文件已录制15秒，自动停止！");
            }
        });
        preview.startRecord();
        time = 0;
        handler.postDelayed(runnable, 100);
    }

    @Override
    public void stopRecorder(View view) {
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, true);
        preview.stopRecord();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void selectIndex(int index) {
        if (index == 0) {
            ActivityUtils.getCameraMain(activity, true, true);
        } else if (index == 2) {
            ActivityUtils.getCameraPhoto(true, true);
        }
        back(null);
    }

    @Override
    public void selectVideo(View view) {
        ActivityUtils.getCameraVideos();
        back(view);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !videoBinding.getIsRecorder()) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = () -> {
                    SCToastUtil.showToastBlack(activity, "自动聚焦超时,请调整合适的位置拍摄！");
                    isFoucing = false;
                    mOverCameraView.setFoucuing(false);
                    mOverCameraView.disDrawTouchFocusRect();
                };
                //设置聚焦超时
                mHandler.postDelayed(mRunnable, 3000);
            }
        }
        return false;
    }

    // 自动对焦
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
        }
    };
    private Thread mThread = null;
    private boolean exit = false;

    private void getVideoFile(File file) {// 获得视频文件
        mThread = new Thread(() -> file.listFiles(file1 -> {
            if (!exit) {
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
                        videoBinding.setVideoPath(videoInfoList.get(0).getPath());
                        exit = true;
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
}
