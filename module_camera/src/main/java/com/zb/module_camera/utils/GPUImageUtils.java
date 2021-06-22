package com.zb.module_camera.utils;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;

public class GPUImageUtils {
    private List<String> handleImageList = new ArrayList<>();
    private int index = 0;
    private List<String> imageList;
    private RxAppCompatActivity mActivity;
    private int mFilmType;
    private CallBack mCallBack;

    public GPUImageUtils(RxAppCompatActivity activity, List<String> images, int filmType, CallBack callBack) {
        mActivity = activity;
        mFilmType = filmType;
        mCallBack = callBack;
        imageList = images;
        handleImageList.clear();
        index = 0;
    }

    public void getGPUImage() {
        GPUImage gpuImage = new GPUImage(mActivity);
        Glide.with(mActivity).asBitmap().load(imageList.get(index)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    gpuImage.setImage(resource);
                    if (mFilmType == 1)
                        gpuImage.setFilter(new GPUImageMonochromeFilter(0.57f, new float[]{0.6f, 0.45f, 0.3f, 1.f}));
                    else if (mFilmType == 2) {
                        GPUImageColorBalanceFilter filter = new GPUImageColorBalanceFilter();
                        filter.setMidtones(new float[]{0.46f, 0.23f, 0.15f});
                        gpuImage.setFilter(filter);
                    } else if (mFilmType == 3)
                        gpuImage.setFilter(new GPUImageContrastFilter(1.49f));
                    else
                        gpuImage.setFilter(new GPUImageSaturationFilter(0.5f));
                    Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
                    String outPutUrl = BaseActivity.getImageFile().getAbsolutePath();
                    File file = new File(outPutUrl);
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.flush();
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                                handleImageList.add(outPutUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    mActivity.runOnUiThread(() -> {
                        index++;
                        if (index < imageList.size()) {
                            getGPUImage();
                        } else {
                            mCallBack.success(handleImageList);
                        }
                    });
                });
            }
        });

    }

    public interface CallBack {
        void success(List<String> handleImageList);
    }
}
