package com.zb.module_camera.utils;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zb.lib_base.utils.SCToastUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import androidx.appcompat.app.AppCompatActivity;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private AppCompatActivity context;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    /**
     * 预览尺寸集合
     */
    private final SizeMap mPreviewSizes = new SizeMap();
    private Size mPreviewSize = null;
    /**
     * 图片尺寸集合
     */
    private final SizeMap mPictureSizes = new SizeMap();
    /**
     * 屏幕旋转显示角度
     */
    private int mDisplayOrientation;
    /**
     * 设备屏宽比
     */
    private AspectRatio mAspectRatio;

    private Camera.Parameters parameters;

    private MediaRecorder mRecorder;//音视频录制类
    public String videoPath = "";

    public CameraPreview(AppCompatActivity context, Camera mCamera, int x, int y) {
        super(context);
        this.context = context;
        this.mCamera = mCamera;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        mAspectRatio = AspectRatio.of(x, y);
        init();
    }

    public CameraPreview(AppCompatActivity context, Camera mCamera, MediaRecorder mRecorder, int x, int y) {
        super(context);
        this.context = context;
        this.mCamera = mCamera;
        this.mRecorder = mRecorder;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        mAspectRatio = AspectRatio.of(x, y);
        init();
    }

    private void init() {
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mDisplayOrientation = context.getWindowManager().getDefaultDisplay().getRotation();

        parameters = mCamera.getParameters();
        // 获取所有支持的预览尺寸
        mPreviewSizes.clear();
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            mPreviewSizes.add(new Size(size.width, size.height));
        }
        mPreviewSize = chooseOptimalSize(mPreviewSizes.sizes(mAspectRatio));
        parameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Log.i("previewSize", mPreviewSize.getWidth() + "" + mPreviewSize.getHeight());
        //设置预览方向
        mCamera.setDisplayOrientation(90);

        List<String> focusModesList = parameters.getSupportedFocusModes();
        //增加对聚焦模式的判断
        if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(parameters);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            //获取所有支持的图片尺寸
            mPictureSizes.clear();
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                mPictureSizes.add(new Size(size.width, size.height));
            }
            Size pictureSize = mPictureSizes.sizes(mAspectRatio).last();
            //设置相机参数
            parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setRotation(90);
            mCamera.setParameters(parameters);
            //把这个预览效果展示在SurfaceView上面
            mCamera.setPreviewDisplay(holder);
            //开启预览效果
            mCamera.startPreview();


        } catch (IOException e) {
            releaseCamera();
            releaseMediaRecorder();
            Log.e("CameraPreview", "相机预览错误: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        //停止预览效果
        mCamera.stopPreview();
        //重新设置预览效果
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    // 释放照相机资源
    public void releaseCamera() {
        try {
            if (null != mCamera) {
                mHolder.removeCallback(this);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.release();
            }
        } catch (RuntimeException e) {
        } finally {
            mCamera = null;
        }
    }

    /**
     * 开始录制
     */
    public void startRecord() {
        try {
            if (mRecorder != null) {
                mCamera.unlock();
                mRecorder.setCamera(mCamera);
                mRecorder.setOrientationHint(90);
                // 设置音频采集方式
                mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                //设置视频的采集方式
                mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                //设置文件的输出格式
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                mRecorder.setVideoSize(640, 480);
                mRecorder.setVideoEncodingBitRate(1024 * 1024);
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                long maxFileSize = Long.parseLong(3 * 1024 * 1024 + "");
                mRecorder.setVideoFrameRate(15);
                mRecorder.setMaxFileSize(maxFileSize);
                mRecorder.setMaxDuration(15*1000);
                String cameraPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
                //相册文件夹
                File cameraFolder = new File(cameraPath);
                if (!cameraFolder.exists()) {
                    cameraFolder.mkdirs();
                }
                //保存的图片文件
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                videoPath = cameraFolder.getAbsolutePath() + File.separator + "Video_" + simpleDateFormat.format(new Date()) + ".mp4";
                //设置输出文件的路径
                mRecorder.setOutputFile(videoPath);
                mRecorder.setPreviewDisplay(mHolder.getSurface());
            }
            //准备录制
            mRecorder.prepare();
            //开始录制
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止录制
     */
    public void stopRecord() {
        try {
            //停止录制
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 释放拍摄资源
    public void releaseMediaRecorder() {
        try {
            if (mRecorder != null) {
                mRecorder.release();
            }
        } catch (RuntimeException e) {
        } finally {
            mRecorder = null;
        }
    }


    /**
     * 注释：获取设备屏宽比
     * 时间：2019/3/4 0004 12:55
     * 作者：郭翰林
     */
    private AspectRatio getDeviceAspectRatio(Activity activity) {
        int width = activity.getWindow().getDecorView().getWidth();
        int height = activity.getWindow().getDecorView().getHeight();
        return AspectRatio.of(height, width);
    }

    /**
     * 注释：选择合适的预览尺寸
     * 时间：2019/3/4 0004 11:25
     * 作者：郭翰林
     *
     * @param sizes
     * @return
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private Size chooseOptimalSize(SortedSet<Size> sizes) {
        int desiredWidth;
        int desiredHeight;
        final int surfaceWidth = getWidth();
        final int surfaceHeight = getHeight();
        if (isLandscape(mDisplayOrientation)) {
            desiredWidth = surfaceHeight;
            desiredHeight = surfaceWidth;
        } else {
            desiredWidth = surfaceWidth;
            desiredHeight = surfaceHeight;
        }
        Size result = null;
        for (Size size : sizes) {
            if (desiredWidth <= size.getWidth() && desiredHeight <= size.getHeight()) {
                return size;
            }
            result = size;
        }
        return result;
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @param orientationDegrees Orientation in degrees (0,90,180,270)
     * @return True if in landscape, false if portrait
     */
    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == 90 ||
                orientationDegrees == 270);
    }

}
