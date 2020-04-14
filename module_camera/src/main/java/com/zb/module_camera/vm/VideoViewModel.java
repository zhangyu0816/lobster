package com.zb.module_camera.vm;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
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

import androidx.databinding.ViewDataBinding;

public class VideoViewModel extends BaseViewModel implements VideoVMInterface, View.OnTouchListener {
    private CameraVideoBinding videoBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private boolean isFoucing = false; // 是否正在聚焦
    private boolean isRecorder = false; // 拍照标记
    private boolean isFlashing = false; // 是否开启闪光灯
    private OverCameraView mOverCameraView; // 聚焦视图
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private int cameraPosition = 1;// 前后置摄像头
    private int _position = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int x = 16, y = 9;

    private MediaRecorder mRecorder;//音视频录制类

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        videoBinding = (CameraVideoBinding) binding;
        mBinding.setVariable(BR.sizeIndex, 0);
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, false);

        mCamera = Camera.open();
        mRecorder = new MediaRecorder();
        preview = new CameraPreview(activity, mCamera, mRecorder, x, y);
        mOverCameraView = new OverCameraView(activity);
        videoBinding.cameraLayout.addView(preview);
        videoBinding.cameraLayout.addView(mOverCameraView);
        videoBinding.cameraLayout.setOnTouchListener(this);
        AdapterBinding.viewSize(videoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * 16f / 9f));
    }

    @Override
    public void cancel(View view) {
        if (isRecorder || videoBinding.getIsFinish()) {
            reset(view);
        } else {
            DataCleanManager.deleteFile(new File(preview.videoPath));
            try {
                if (isFlashing) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);
                }
            } catch (Exception e) {
            }
            preview.releaseCamera();
            preview.releaseMediaRecorder();
            activity.finish();
        }
    }

    @Override
    public void reset(View view) {
        DataCleanManager.deleteFile(new File(preview.videoPath));
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        videoBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(_position);
        mRecorder = new MediaRecorder();
        preview = new CameraPreview(activity, mCamera, mRecorder, x, y);
        videoBinding.cameraLayout.addView(preview);
        videoBinding.cameraLayout.addView(mOverCameraView);
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, false);
    }

    @Override
    public void upload(View view) {

    }

    @Override
    public void changeSizeIndex(int index) {
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        videoBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(_position);
        mRecorder = new MediaRecorder();
        if (index == 0) {
            x = 16;
            y = 9;
            AdapterBinding.viewSize(videoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * 16f / 9f));
        } else {
            x = 4;
            y = 3;
            AdapterBinding.viewSize(videoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * 4f / 3f));
        }
        preview = new CameraPreview(activity, mCamera, mRecorder, x, y);
        videoBinding.cameraLayout.addView(preview);
        videoBinding.cameraLayout.addView(mOverCameraView);

        mBinding.setVariable(BR.sizeIndex, index);
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
        videoBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(position);//打开当前选中的摄像头
        mRecorder = new MediaRecorder();
        preview = new CameraPreview(activity, mCamera, mRecorder, x, y);
        videoBinding.cameraLayout.addView(preview);
        videoBinding.cameraLayout.addView(mOverCameraView);
    }

    @Override
    public void createRecorder(View view) {
        if (videoBinding.getIsFinish()) return;
        isRecorder = true;
        mBinding.setVariable(BR.isRecorder, true);
        preview.startRecord();
    }

    @Override
    public void stopRecorder(View view) {
        isRecorder = false;
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, true);
        preview.stopRecord();
    }

    @Override
    public void selectIndex(int index) {
        if (index == 0) {
            ActivityUtils.getCameraMain(activity, true);
        } else if (index == 2) {
            ActivityUtils.getCameraPhoto();
        }
        cancel(null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !isRecorder) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = () -> {
                    SCToastUtil.showToast(activity, "    自动聚焦超时,请调整合适的位置拍摄！    ");
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

}
