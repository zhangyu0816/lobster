package com.yimi.rentme.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseContextReceiver;
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

public class ForegroundLiveService extends Service {

    private PhotoManager mPhotoManager;

    public ForegroundLiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ForegroundLiveService", "Service");

        mPhotoManager = new PhotoManager(MineApp.activity, new PhotoManager.OnUpLoadImageListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(PhotoFile file) {
                FilmResourceDb filmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
                mPhotoManager.deleteAllFile();
                Log.e("ForegroundLiveService","uploadBackSuccess");
                filmResourceDb.updateImages(file.getCameraFilmId(), file.getSrcFilePath(), false);
                MineApp.getApp().getFixedThreadPool().execute(() -> saveCameraFilmResource(file.getCameraFilmId(), file.getWebUrl()));
            }

            @Override
            public void onError(PhotoFile file) {
                mPhotoManager.addFileUpload(0, new File(file.getSrcFilePath()));
            }
        });
        mPhotoManager.setGPU(true);
        mPhotoManager.setNeedProgress(false);
        new BaseContextReceiver(MineApp.sContext, "lobster_startGPU") {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("ForegroundLiveService", "lobster_startGPU");
                getGPUImage();
            }
        };
        getGPUImage();
    }

    private void getGPUImage() {
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            FilmResourceDb filmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
            List<FilmResource> filmResourceList = filmResourceDb.getAllCameraFilm();
            if (filmResourceList.size() > 0) {
                for (FilmResource filmResource : filmResourceList) {
                    if (filmResource.getImages().isEmpty()) {
                        Log.e("ForegroundLiveService","deleteFilm");
                        filmResourceDb.deleteFilm(filmResource.getCameraFilmId());
                    } else {
                        List<String> resourceImageList = Arrays.asList(filmResource.getImages().split("#"));
                        long cameraFilmId = filmResource.getCameraFilmId();
                        int cameraFilmType = filmResource.getCameraFilmType();
                        for (String image : resourceImageList) {
                            File file = new File(image);
                            if (file.exists()) {
                                if (image.contains("filmImages")) {
                                    mPhotoManager.addFileUploadForFilm(0, file, cameraFilmId, cameraFilmType);
                                } else {
                                    Glide.with(MineApp.sContext).asBitmap().load(image).into(new SimpleTarget<Bitmap>() {

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                            super.onLoadCleared(placeholder);
                                            if (resourceImageList.size() == 1)
                                                filmResourceDb.deleteFilm(cameraFilmId);
                                            else
                                                filmResourceDb.updateImages(cameraFilmId, image, false);
                                        }

                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            FilmResourceDb filmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
                                            GPUImage gpuImage = new GPUImage(MineApp.sContext);
                                            gpuImage.setImage(resource);
                                            if (cameraFilmType == 1)
                                                gpuImage.setFilter(new GPUImageMonochromeFilter(0.57f, new float[]{0.6f, 0.45f, 0.3f, 1.f}));
                                            else if (cameraFilmType == 2) {
                                                GPUImageColorBalanceFilter filter = new GPUImageColorBalanceFilter();
                                                filter.setMidtones(new float[]{0.46f, 0.23f, 0.15f});
                                                gpuImage.setFilter(filter);
                                            } else if (cameraFilmType == 3)
                                                gpuImage.setFilter(new GPUImageContrastFilter(1.49f));
                                            else
                                                gpuImage.setFilter(new GPUImageSaturationFilter(0.5f));
                                            Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
                                            File file1 = BaseActivity.getFilmImageFile();
                                            try {
                                                FileOutputStream os = new FileOutputStream(file1);
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                                                os.flush();
                                                os.close();
                                                filmResourceDb.changeImages(cameraFilmId, image, file1.getAbsolutePath());
                                                mPhotoManager.addFileUploadForFilm(0, file1, cameraFilmId, cameraFilmType);
                                            } catch (Exception e) {
                                                if (resourceImageList.size() == 1)
                                                    filmResourceDb.deleteFilm(cameraFilmId);
                                                else
                                                    filmResourceDb.updateImages(cameraFilmId, image, false);
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } else {
                                filmResourceDb.updateImages(cameraFilmId, image, false);
                            }
                        }
                    }
                }
            }
        });
    }

    private void saveCameraFilmResource(long filmId, String image) {
        saveCameraFilmResourceApi api = new saveCameraFilmResourceApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                Log.e("ForegroundLiveService","uploadSuccess");
            }
        }, MineApp.activity).setCameraFilmId(filmId).setImage(image);
        HttpManager.getInstance().doHttpDeal(api);
    }


}