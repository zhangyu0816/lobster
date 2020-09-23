package com.zb.module_camera.vm;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.BR;
import com.zb.module_camera.databinding.CameraPhotoBinding;
import com.zb.module_camera.iv.PhotoVMInterface;
import com.zb.module_camera.utils.CameraPreview;
import com.zb.module_camera.utils.OverCameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.databinding.ViewDataBinding;

public class PhotoViewModel extends BaseViewModel implements PhotoVMInterface, View.OnTouchListener {
    public boolean isMore;
    public boolean showBottom;
    public boolean showVideo;
    private CameraPhotoBinding photoBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private boolean isFoucing = false; // 是否正在聚焦
    private boolean isTakePhoto = false; // 拍照标记
    private boolean isFlashing = false; // 是否开启闪光灯
    private OverCameraView mOverCameraView; // 聚焦视图
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private byte[] imageData; // 图片流暂存
    private int cameraPosition = 1;// 前后置摄像头
    private int _position = Camera.CameraInfo.CAMERA_FACING_BACK;
    private float x = 16f, y = 9f;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        photoBinding = (CameraPhotoBinding) binding;
        mBinding.setVariable(BR.sizeIndex, 0);
        mBinding.setVariable(BR.lightIndex, 0);
        mBinding.setVariable(BR.isCreate, false);
        photoBinding.cameraLayout.setOnTouchListener(this);
        AdapterBinding.viewSize(photoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * x / y));
        getPermissions();
    }

    private void initCamera() {
        photoBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(_position);
        preview = new CameraPreview(activity, mCamera, (int) x, (int) y);
        mOverCameraView = new OverCameraView(activity);
        photoBinding.cameraLayout.addView(preview);
        photoBinding.cameraLayout.addView(mOverCameraView);
    }

    @Override
    public void back(View view) {
        if (isTakePhoto) {
            reset(view);
        } else {
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
    }

    @Override
    public void reset(View view) {
        mBinding.setVariable(BR.isCreate, false);
        try {
            mCamera.startPreview();
        } catch (Exception ignored) {
        }
        imageData = null;
        isTakePhoto = false;
    }

    @Override
    public void changeSizeIndex(int index) {
        mBinding.setVariable(BR.sizeIndex, index);
        preview.releaseCamera();
        if (index == 0) {
            x = 16f;
            y = 9f;
        } else {
            x = 4f;
            y = 3f;
        }
        AdapterBinding.viewSize(photoBinding.cameraLayout, MineApp.W, (int) (MineApp.W * x / y));
        getPermissions();
    }

    @Override
    public void changeLightIndex(int index) {
        isFlashing = index == 1;
        mBinding.setVariable(BR.lightIndex, index);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(isFlashing ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
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
        _position = position;
        getPermissions();
    }

    @Override
    public void selectIndex(int index) {
        if (index == 0) {
            ActivityUtils.getCameraMain(activity, isMore, showBottom, showVideo);
        } else if (index == 1) {
            ActivityUtils.getCameraVideo(true);
        }
        back(null);
    }

    @Override
    public void createPhoto(View view) {
        isTakePhoto = true;
        mBinding.setVariable(BR.isCreate, true);
        //调用相机拍照
        try {
            mCamera.takePicture(null, null, null, (data, camera1) -> {
                imageData = data;
                //停止预览
                mCamera.stopPreview();
            });
        } catch (Exception ignored) {

        }
    }

    @Override
    public void upload(View view) {
        String cameraPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
        //相册文件夹
        File cameraFolder = new File(cameraPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        //保存的图片文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imagePath = cameraFolder.getAbsolutePath() + File.separator + "IMG_" + simpleDateFormat.format(new Date()) + ".jpg";
        File imageFile = new File(imagePath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    if (MineApp.toPublish && !MineApp.toContinue && !MineApp.isChat) {
                        MineApp.cameraType = 2;
                        MineApp.isMore = false;
                        MineApp.filePath = imagePath;
                        ActivityUtils.getHomePublishImage();
                    } else {
                        Intent data = new Intent("lobster_camera");
                        data.putExtra("cameraType", 2);
                        data.putExtra("filePath", imagePath);
                        activity.sendBroadcast(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isTakePhoto = false;
            back(view);
        }
    }

    // 点击对焦
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = () -> {
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

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限、相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        initCamera();
    }
}
