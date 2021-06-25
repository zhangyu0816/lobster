package com.zb.lib_base.utils;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.saveCameraFilmResourceApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.FilmResourceDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.FilmResource;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.realm.Realm;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;

public class GPUImageUtils {
    private RxAppCompatActivity mActivity;
    private PhotoManager mPhotoManager;
    private boolean isUpdate = false;
    private FilmResource mFilmResource;
    private List<String> resourceImageList = new ArrayList<>();
    private FilmResourceDb mFilmResourceDb;

    public GPUImageUtils(RxAppCompatActivity activity) {
        mActivity = activity;
        mFilmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
        mPhotoManager = new PhotoManager(activity, () -> {
            PhotoFile photoFile = mPhotoManager.getPhotoFiles().get(0);
            mPhotoManager.deleteAllFile();
            saveCameraFilmResource(mFilmResource.getCameraFilmId(), photoFile.getWebUrl());

            if (resourceImageList.size() == 1)
                mFilmResourceDb.deleteFilm(mFilmResource.getCameraFilmId());
            else
                mFilmResourceDb.updateImages(mFilmResource.getCameraFilmId(), photoFile.getSrcFilePath(), false);
            getGPUImage();
        });
        mPhotoManager.setNeedProgress(false);
    }

    public void start() {
        if (!isUpdate) {
            isUpdate = true;
            getGPUImage();
        }
    }

    private void getGPUImage() {
        GPUImage gpuImage = new GPUImage(mActivity);
        List<FilmResource> filmResourceList = mFilmResourceDb.getAllCameraFilm();
        if (filmResourceList.size() > 0) {
            mFilmResource = filmResourceList.get(0);
            resourceImageList = Arrays.asList(mFilmResource.getImages().split("#"));
            String image = resourceImageList.get(0);
            File file = new File(image);
            if (file.exists()) {
                Glide.with(MineApp.sContext).asBitmap().load(image).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        gpuImage.setImage(resource);
                        if (mFilmResource.getCameraFilmType() == 1)
                            gpuImage.setFilter(new GPUImageMonochromeFilter(0.57f, new float[]{0.6f, 0.45f, 0.3f, 1.f}));
                        else if (mFilmResource.getCameraFilmType() == 2) {
                            GPUImageColorBalanceFilter filter = new GPUImageColorBalanceFilter();
                            filter.setMidtones(new float[]{0.46f, 0.23f, 0.15f});
                            gpuImage.setFilter(filter);
                        } else if (mFilmResource.getCameraFilmType() == 3)
                            gpuImage.setFilter(new GPUImageContrastFilter(1.49f));
                        else
                            gpuImage.setFilter(new GPUImageSaturationFilter(0.5f));
                        Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
                        File file = new File(image);
                        try {
                            FileOutputStream os = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.flush();
                            os.close();
                            mActivity.runOnUiThread(() -> mPhotoManager.addFileUpload(0, file));
                        } catch (Exception e) {
                            if (resourceImageList.size() == 1)
                                mFilmResourceDb.deleteFilm(mFilmResource.getCameraFilmId());
                            else
                                mFilmResourceDb.updateImages(mFilmResource.getCameraFilmId(), image, false);
                            getGPUImage();
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                if (resourceImageList.size() == 1)
                    mFilmResourceDb.deleteFilm(mFilmResource.getCameraFilmId());
                else
                    mFilmResourceDb.updateImages(mFilmResource.getCameraFilmId(), image, false);
                getGPUImage();
            }
        } else {
            isUpdate = false;
        }
    }

    private void saveCameraFilmResource(long filmId, String image) {
        saveCameraFilmResourceApi api = new saveCameraFilmResourceApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
            }
        }, mActivity).setCameraFilmId(filmId).setImage(image);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
