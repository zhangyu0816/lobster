package com.zb.module_camera.vm;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.databinding.AcPhotoStudioBinding;
import com.zb.module_camera.iv.PhotoStudioVMInterface;
import com.zb.module_camera.utils.CameraPreview;

import androidx.databinding.ViewDataBinding;

public class PhotoStudioViewModel extends BaseViewModel implements PhotoStudioVMInterface, View.OnTouchListener {
    private AcPhotoStudioBinding mBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean isFlashing = false; // 是否开启闪光灯
    private byte[] imageData; // 图片流暂存

    private GestureDetector gesturedetector = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcPhotoStudioBinding) binding;
        mBinding.cameraLayout.setOnTouchListener(this);
        mBinding.setFilmIndex(0);
        initCamera();

        gesturedetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (distanceY > 0) {
                    changeZoomUp(mBinding.getRoot());
                } else {
                    changeZoomDown(mBinding.getRoot());
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        mBinding.whileRelative.setOnTouchListener((v, event) -> {
            gesturedetector.onTouchEvent(event);
            return true;
        });

    }

    @Override
    public void back(View view) {
        super.back(view);
        try {
            if (isFlashing) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }
        } catch (Exception ignored) {
        }
        if (preview != null)
            preview.releaseCamera();
        activity.finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private void initCamera() {
        mBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(cameraPosition);
        preview = new CameraPreview(activity, mCamera, 16, 9);
        mBinding.cameraLayout.addView(preview);
    }

    @Override
    public void changeLightIndex(View view) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                isFlashing = false;
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                isFlashing = true;
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            SCToastUtil.showToast(activity, "该设备不支持闪光灯", true);
        }
    }

    @Override
    public void changeCameraId(View view) {
        //切换前后摄像头
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraCount >= 2 && cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
                    openCamera();
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    openCamera();
                    break;
                }
            }
        }
    }

    private void openCamera() {
        preview.releaseCamera();
        initCamera();
    }

    @Override
    public void changeZoomUp(View view) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            int zoom = parameters.getZoom();
            zoom++;
            parameters.setZoom(zoom);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
        }
    }

    @Override
    public void changeZoomDown(View view) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            int zoom = parameters.getZoom();
            zoom--;
            parameters.setZoom(zoom);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
        }
    }

    @Override
    public void selectFilm(int index) {
        mBinding.setFilmIndex(index);
    }

    @Override
    public void tackPhoto(View view) {
        //调用相机拍照
        try {
            mCamera.takePicture(null, null, null, (data, camera1) -> {
                imageData = data;
                //停止预览
                mCamera.stopPreview();

//                File imageFile = BaseActivity.getImageFile();

            });
        } catch (Exception ignored) {

        }
    }
}
