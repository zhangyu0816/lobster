package com.zb.module_mine.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.utils.CameraPreview;
import com.zb.module_camera.utils.OverCameraView;
import com.zb.module_mine.BR;
import com.zb.module_mine.databinding.MineRealNameBinding;
import com.zb.module_mine.iv.RealNameVMInterface;

import androidx.databinding.ViewDataBinding;

public class RealNameViewModel extends BaseViewModel implements RealNameVMInterface, View.OnTouchListener {
    private MineRealNameBinding nameBinding;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        nameBinding = (MineRealNameBinding) binding;
        nameBinding.cameraLayout.setOnTouchListener(this);
        AdapterBinding.viewSize(nameBinding.cameraLayout, MineApp.W, (int) (MineApp.W * 4f / 3f));
        initCamera();
    }

    private void initCamera() {
        nameBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        preview = new CameraPreview(activity, mCamera, 4, 3);
        mOverCameraView = new OverCameraView(activity);
        nameBinding.cameraLayout.addView(preview);
        nameBinding.cameraLayout.addView(mOverCameraView);

        animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(nameBinding.tvTime, "scaleX", 0, 1).setDuration(700);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(nameBinding.tvTime, "scaleY", 0, 1).setDuration(700);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);


        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                nameBinding.tvTime.setText(millisUntilFinished / 1000 + "");
                animatorSet.start();
            }

            @Override
            public void onFinish() {
                isTakePhoto = true;
                nameBinding.tvTime.setScaleX(0);
                nameBinding.tvTime.setScaleY(0);
                nameBinding.btnLinear.setVisibility(View.VISIBLE);
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
        preview.releaseCamera();
        animatorSet.cancel();
        timer.cancel();
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
        nameBinding.btnLinear.setVisibility(View.GONE);
        timer.start();
    }

    @Override
    public void upload(View view) {

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
