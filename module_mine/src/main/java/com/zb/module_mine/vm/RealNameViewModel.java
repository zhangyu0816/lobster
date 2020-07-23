package com.zb.module_mine.vm;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.humanFaceApi;
import com.zb.lib_base.api.humanFaceStatusApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.utils.CameraPreview;
import com.zb.module_camera.utils.OverCameraView;
import com.zb.module_mine.BR;
import com.zb.module_mine.databinding.MineRealNameBinding;
import com.zb.module_mine.iv.RealNameVMInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.databinding.ViewDataBinding;

public class RealNameViewModel extends BaseViewModel implements RealNameVMInterface, View.OnTouchListener {
    private MineRealNameBinding mBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private OverCameraView mOverCameraView; // 聚焦视图
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private byte[] imageData; // 图片流暂存
    private boolean isFoucing = false;
    private boolean isTakePhoto = false;
    private CountDownTimer timer;
    private AnimatorSet animatorSet;
    private PhotoManager photoManager;
    private String cameraPath = "";
    private File cameraFolder;
    private String imagePath = "";
    private File imageFile;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineRealNameBinding) binding;
        mBinding.cameraLayout.setOnTouchListener(this);
        AdapterBinding.viewSize(mBinding.cameraLayout, MineApp.W, (int) (MineApp.W * 4f / 3f));
        getPermissions();
        humanFaceStatus();
        photoManager = new PhotoManager(activity, () -> {
            humanFace(photoManager.jointWebUrl(","));
        });
        cameraPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
        cameraFolder = new File(cameraPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        imagePath = cameraFolder.getAbsolutePath() + File.separator + "real.jpg";
        imageFile = new File(imagePath);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission( "虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            back(null);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        initCamera();
    }

    private void initCamera() {
        mBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        preview = new CameraPreview(activity, mCamera, 4, 3);
        mOverCameraView = new OverCameraView(activity);
        mBinding.cameraLayout.addView(preview);
        mBinding.cameraLayout.addView(mOverCameraView);

        animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBinding.tvTime, "scaleX", 0, 1).setDuration(700);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBinding.tvTime, "scaleY", 0, 1).setDuration(700);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);


        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.tvTime.setText(millisUntilFinished / 1000 + "");
                animatorSet.start();
            }

            @Override
            public void onFinish() {
                isTakePhoto = true;
                mBinding.tvTime.setScaleX(0);
                mBinding.tvTime.setScaleY(0);
                mBinding.btnLinear.setVisibility(View.VISIBLE);
                //调用相机拍照
                mCamera.takePicture(null, null, null, (data, camera1) -> {
                    imageData = data;
                    //停止预览
                    mCamera.stopPreview();
                    timer.cancel();
                });
            }
        };

    }

    @Override
    public void back(View view) {
        super.back(view);
        if (preview != null)
            preview.releaseCamera();
        if (animatorSet != null)
            animatorSet.cancel();
        if (timer != null)
            timer.cancel();
        DataCleanManager.deleteFile(imageFile);
        activity.finish();
    }

    @Override
    public void toAuthentication(View view) {
        timer.start();
        mBinding.setVariable(BR.step, 2);
    }

    @Override
    public void reset(View view) {
        mCamera.startPreview();
        imageData = null;
        isTakePhoto = false;
        mBinding.btnLinear.setVisibility(View.GONE);
        timer.start();
    }

    @Override
    public void upload(View view) {
        //保存的图片文件
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
                    photoManager.addFileUpload(0, imageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isTakePhoto = false;
        }
    }

    @Override
    public void humanFaceStatus() {
        humanFaceStatusApi api = new humanFaceStatusApi(new HttpOnNextListener<FaceStatus>() {
            @Override
            public void onNext(FaceStatus o) {
                mBinding.setVariable(BR.checkStatus, o.getIsChecked());
                mBinding.setVariable(BR.remind, "虾菇仔细的看了你的照片，发现不合格哦！");
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.setVariable(BR.checkStatus, -1);
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void humanFace(String image) {
        humanFaceApi api = new humanFaceApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "人脸认证已提交", true);
                back(null);
            }
        }, activity).setFaceImage(image);
        HttpManager.getInstance().doHttpDeal(api);
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
}
