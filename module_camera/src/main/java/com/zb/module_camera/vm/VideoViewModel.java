package com.zb.module_camera.vm;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
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

import androidx.databinding.ViewDataBinding;

public class VideoViewModel extends BaseViewModel implements VideoVMInterface, View.OnTouchListener {
    public boolean showBottom;
    private CameraVideoBinding videoBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private boolean isFoucing = false; // 是否正在聚焦
    private OverCameraView mOverCameraView; // 聚焦视图
    private Handler mHandler = new Handler();
    private int cameraPosition = 1;// 前后置摄像头
    private int _position = Camera.CameraInfo.CAMERA_FACING_BACK;
    private float x = 16f, y = 9f;

    private MediaRecorder mRecorder;//音视频录制类
    private long time = 0;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time += 100;
            int s = (int) (time / 1000);
            int ms = (int) (time - s * 1000) / 10;
            videoBinding.setSecond("已录制" + s + "." + (ms < 10 ? "0" + ms : ms + "") + "S");
            mHandler.postDelayed(this, 100);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
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
        MineApp.videoInfoList.clear();

        getAllLocalVideos();
    }

    private void initCamera() {
        videoBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(_position);
        mRecorder = new MediaRecorder();
        preview = new CameraPreview(activity, mCamera, mRecorder, (int) x, (int) y, _position);
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
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
            mHandler.removeCallbacks(runnable);
        mHandler = null;
    }

    @Override
    public void reset(View view) {
        mBinding.setVariable(BR.isRecorder, false);
        mBinding.setVariable(BR.isFinish, false);
        mCamera.startPreview();
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
        int cameraCount;
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
                SCToastUtil.showToast(activity, "视频文件已达到3M，自动停止！", true);
            } else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                stopRecorder(view);
                SCToastUtil.showToast(activity, "视频文件已录制20秒，自动停止！", true);
            }
        });
        preview.startRecord();
        time = 0;
        mHandler.postDelayed(runnable, 100);
    }

    @Override
    public void stopRecorder(View view) {
        if (time < 1000) {
            reset(view);
            SCToastUtil.showToast(activity, "视频文件录制太短，请重新录制", true);
            return;
        }
        preview.stopRecord();
        mHandler.removeCallbacks(runnable);
        preview.releaseCamera();
        preview.releaseMediaRecorder();
        MineApp.isLocation = false;
        MineApp.showBottom = showBottom;
        mHandler.postDelayed(() -> {
            ActivityUtils.getCameraVideoPlay(preview.videoPath, true, false);
            activity.finish();
        }, 500);
    }

    @Override
    public void selectIndex(int index) {
        if (!videoBinding.getIsRecorder() && !videoBinding.getIsFinish()) {
            if (index == 0) {
                ActivityUtils.getCameraMain(activity, true, true, true);
            } else if (index == 2) {
                ActivityUtils.getCameraPhoto(true, true, true);
            }
            back(null);
        }
    }

    @Override
    public void selectVideo(View view) {
        ActivityUtils.getCameraVideos(showBottom);
        back(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !videoBinding.getIsFinish()) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(1000);
                    activity.runOnUiThread(() -> {
                        isFoucing = false;
                        mOverCameraView.setFoucuing(false);
                        mOverCameraView.disDrawTouchFocusRect();
                    });
                });
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
        }
    };

    /**
     * 获取本地所有的视频
     *
     * @return list
     */
    private void getAllLocalVideos() {
        Runnable ra = () -> {
            String[] projection = {
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };
            //全部图片
            String where = MediaStore.Video.Media.MIME_TYPE + "=?";
            String[] whereArgs = {"video/mp4"};
            Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection, where, whereArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");
            if (cursor == null) {
                return;
            }
            try {
                while (cursor.moveToNext()) {
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                    if (size < 20 * 1024 * 1024) {//<600M
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                        VideoInfo vi = new VideoInfo();
                        vi.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                        vi.setPath(path);
                        MineApp.videoInfoList.add(vi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

            activity.runOnUiThread(() -> {
                if (MineApp.videoInfoList.size() > 0)
                    videoBinding.setVideoPath(MineApp.videoInfoList.get(0).getPath());
            });
        };
        MineApp.getApp().getFixedThreadPool().execute(ra);
    }
}
