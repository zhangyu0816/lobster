package com.zb.module_camera.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.cameraFilmMsCountApi;
import com.zb.lib_base.api.findCameraFilmsApi;
import com.zb.lib_base.api.washResourceApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.model.FilmResource;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FilmDF;
import com.zb.lib_base.windows.FilmRinseDF;
import com.zb.module_camera.databinding.AcPhotoStudioBinding;
import com.zb.module_camera.iv.PhotoStudioVMInterface;
import com.zb.module_camera.utils.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PhotoStudioViewModel extends BaseViewModel implements PhotoStudioVMInterface, View.OnTouchListener {
    private AcPhotoStudioBinding mBinding;
    private Camera mCamera;
    private CameraPreview preview;
    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean isFlashing = false; // 是否开启闪光灯
    private byte[] imageData; // 图片流暂存
    private List<Film> mFilmList = new ArrayList<>();
    private int camerafilmType = 0;//胶卷类型
    private GestureDetector gesturedetector = null;
    private Film mFilm;
    private FilmResource mFilmResource;
    private BaseReceiver washSuccessReceiver;
    private BaseReceiver readFilmMsgReceiver;
    private BaseReceiver addFilmMsgCountReceiver;
    private int filmMsgCount = 0;
    private PropertyValuesHolder pvhTY;
    private ObjectAnimator pvh_bottom;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcPhotoStudioBinding) binding;
        mBinding.cameraLayout.setOnTouchListener(this);
        mBinding.setFilmIndex(0);
        mBinding.setHasFilm(false);
        mBinding.setShowBottom(false);
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
        findCameraFilms();

        washSuccessReceiver = new BaseReceiver(activity, "lobster_washSuccess") {
            @Override
            public void onReceive(Context context, Intent intent) {
                washResource();
            }
        };
        readFilmMsgReceiver = new BaseReceiver(activity, "lobster_readFilmMsg") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("cleanAll", false))
                    filmMsgCount = 0;
                else
                    filmMsgCount--;
                mBinding.setNewsCount(Math.max(0, filmMsgCount));
            }
        };
        addFilmMsgCountReceiver = new BaseReceiver(activity, "lobster_addFilmMsgCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                filmMsgCount++;
                mBinding.setNewsCount(Math.max(0, filmMsgCount));
            }
        };
        cameraFilmMsCount();
    }

    @Override
    public void back(View view) {
        super.back(view);
        try {
            if (isFlashing) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mBinding.setIsFlashing(false);
            }
        } catch (Exception ignored) {
        }
        if (pvh_bottom != null) {
            pvh_bottom.cancel();
            pvh_bottom = null;
        }
        if (preview != null)
            preview.releaseCamera();
        activity.finish();
    }

    public void onDestroy() {
        try {
            washSuccessReceiver.unregisterReceiver();
            readFilmMsgReceiver.unregisterReceiver();
            addFilmMsgCountReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void openCamera() {
        if (preview != null)
            preview.releaseCamera();
        initCamera();
    }

    private void initCamera() {
        mBinding.cameraLayout.removeAllViews();
        mCamera = Camera.open(cameraPosition);
        preview = new CameraPreview(activity, mCamera, 16, 9, cameraPosition);
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
            mBinding.setIsFlashing(isFlashing);
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
        new FilmDF(activity).setFilms(mFilmList).setFilmType(index).setSet(false)
                .setFilmCallBack((filmType, film) -> {
                    camerafilmType = filmType;
                    mFilm = film;
                    activity.runOnUiThread(this::updateFilm);
                }).show(activity.getSupportFragmentManager());
    }

    @Override
    public void setFilm(View view) {
        new FilmDF(activity).setFilms(mFilmList).setFilmType(1).setSet(true).
                setFilmCallBack((filmType, film) -> {
                    camerafilmType = filmType;
                    mFilm = film;
                    activity.runOnUiThread(this::updateFilm);

                }).show(activity.getSupportFragmentManager());
    }

    private void updateFilm() {
        mFilmResource = MineApp.sFilmResourceDb.getCameraFilm(mFilm.getId());
        if (mFilmResource == null) {
            mFilmResource = new FilmResource(mFilm.getId(), "", mFilm.getCamerafilmType());
            MineApp.sFilmResourceDb.saveData(mFilmResource);
        }
        mBinding.setFilmIndex(camerafilmType);
        mBinding.setHasFilm(MineApp.sFilmResourceDb.getImageSize(mFilm.getId()) < MineApp.filmMaxSize);
        boolean has = false;
        for (int i = 0; i < mFilmList.size(); i++) {
            if (mFilmList.get(i).getId() == mFilm.getId()) {
                has = true;
                mFilmList.set(i, mFilm);
            }
        }
        if (!has) {
            mFilmList.add(mFilm);
        }
    }

    @Override
    public void wash(View view) {
        if (mFilm == null) {
            SCToastUtil.showToast(activity, "请选择胶卷", true);
            return;
        }
        if (MineApp.sFilmResourceDb.getImageSize(mFilm.getId()) == 0) {
            SCToastUtil.showToast(activity, "该胶卷尚未使用", true);
            return;
        }
        new FilmRinseDF(activity).setFilm(mFilm).setFilmRinseCallBack(this::washResource).show(activity.getSupportFragmentManager());
    }

    @Override
    public void tackPhoto(View view) {
        if (mFilm == null) {
            SCToastUtil.showToast(activity, "请选择胶卷", true);
            setBottom(view);
            return;
        }
        if (!mBinding.getHasFilm()) {
            new FilmRinseDF(activity).setFilm(mFilm).setFilmRinseCallBack(this::washResource).show(activity.getSupportFragmentManager());
            return;
        }
        //调用相机拍照
        try {
            mCamera.takePicture(null, null, null, (data, camera1) -> {
                imageData = data;
                //停止预览
                mCamera.stopPreview();
                File imageFile = BaseActivity.getImageFile();
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
                            mBinding.viewShadow.setVisibility(View.VISIBLE);
                            MineApp.sFilmResourceDb.updateImages(mFilm.getId(), imageFile.getAbsolutePath(), true);
                            mBinding.setHasFilm(MineApp.sFilmResourceDb.getImageSize(mFilm.getId()) < MineApp.filmMaxSize);
                            MineApp.getApp().getFixedThreadPool().execute(() -> {
                                SystemClock.sleep(200);
                                activity.runOnUiThread(() -> {
                                    mBinding.viewShadow.setVisibility(View.GONE);
                                    mCamera.startPreview();
                                });
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception ignored) {

        }
    }

    @Override
    public void toPhotoWall(View view) {
        if (mFilm == null) {
            SCToastUtil.showToast(activity, "请选择胶卷", true);
            return;
        }
        ActivityUtils.getCameraPhotoWall(mFilm, MineApp.filmMaxSize - MineApp.sFilmResourceDb.getImageSize(mFilm.getId()));
    }

    @Override
    public void toPhotoGroup(View view) {
        ActivityUtils.getCameraPhotoGroup();
    }

    @Override
    public void toMyFilm(View view) {
        ActivityUtils.getCameraPhotoMyFilm(filmMsgCount);
    }

    @Override
    public void setBottom(View view) {
        mBinding.setShowBottom(!mBinding.getShowBottom());
        if (mBinding.getShowBottom()) {
            // 弹出
            pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -DisplayUtils.dip2px(160));
        } else {
            pvhTY = PropertyValuesHolder.ofFloat("translationY", -DisplayUtils.dip2px(160), 0);
        }
        pvh_bottom = ObjectAnimator.ofPropertyValuesHolder(mBinding.bottomLayout, pvhTY).setDuration(200);
        pvh_bottom.start();
    }

    @Override
    public void findCameraFilms() {
        findCameraFilmsApi api = new findCameraFilmsApi(new HttpOnNextListener<List<Film>>() {
            @Override
            public void onNext(List<Film> o) {
                mFilmList.clear();
                mFilmList.addAll(o);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mFilmList.clear();
                }
            }
        }, activity).setIsEnable(0).setPageNo(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void washResource() {
        washResourceApi api = new washResourceApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_startGPU"));
                SCToastUtil.showToast(activity,"已提交冲洗",true);
                findCameraFilms();
                mFilm = null;
                camerafilmType = 0;
                mBinding.setFilmIndex(camerafilmType);
                mBinding.setHasFilm(false);
            }
        }, activity).setCameraFilmId(mFilm.getId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cameraFilmMsCount() {
        cameraFilmMsCountApi api = new cameraFilmMsCountApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                filmMsgCount = o;
                mBinding.setNewsCount(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
