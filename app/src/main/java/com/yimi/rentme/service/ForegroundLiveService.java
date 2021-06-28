package com.yimi.rentme.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yimi.rentme.R;
import com.zb.lib_base.activity.BaseReceiver;
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
import androidx.core.app.NotificationCompat;
import io.realm.Realm;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;

public class ForegroundLiveService extends Service {

    public static final int NOTIFICATION_ID = 0x11;

    public ForegroundLiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //API 18以上，发送Notification并将其置为前台后，启动InnerService
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MineApp.NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(getString(R.string.app_name));
        builder.setContentTitle(getString(R.string.app_name_sub));
        builder.setContentText(getString(R.string.app_name_running));
        builder.setWhen(System.currentTimeMillis()); //发送时间
        startForeground(NOTIFICATION_ID, builder.build());
        startService(new Intent(this, InnerService.class));
    }

    public static class InnerService extends Service {
        private FilmResourceDb sFilmResourceDb;
        private PhotoManager mPhotoManager;
        private List<String> resourceImageList = new ArrayList<>();
        private long cameraFilmId;
        private boolean isUpdate = false;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e("InnerService", "Service");

            sFilmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
            mPhotoManager = new PhotoManager(MineApp.activity, new PhotoManager.OnUpLoadImageListener() {
                @Override
                public void onSuccess() {
                    PhotoFile photoFile = mPhotoManager.getPhotoFiles().get(0);
                    mPhotoManager.deleteAllFile();
                    saveCameraFilmResource(cameraFilmId, photoFile.getWebUrl());
                    MineApp.activity.runOnUiThread(() -> {
                        if (resourceImageList.size() == 1)
                            sFilmResourceDb.deleteFilm(cameraFilmId);
                        else
                            sFilmResourceDb.updateImages(cameraFilmId, photoFile.getSrcFilePath(), false);
                        getGPUImage();
                    });
                }

                @Override
                public void onError(PhotoFile file) {
                    mPhotoManager.addFileUpload(0, new File(file.getSrcFilePath()));
                }
            });
            mPhotoManager.setGPU(true);
            mPhotoManager.setNeedProgress(false);
            new BaseReceiver(MineApp.activity, "lobster_startGPU") {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("InnerService", "55555555555");
                    start();
                }
            };
            start();
        }

        private void start() {
            Log.e("InnerService", "2222222222222");
            if (!isUpdate) {
                Log.e("InnerService", "33333333333333");
                isUpdate = true;
                getGPUImage();
            }
        }

        private void getGPUImage() {
            GPUImage gpuImage = new GPUImage(MineApp.activity);
            List<FilmResource> filmResourceList = sFilmResourceDb.getAllCameraFilm();
            Log.e("InnerService", "44444444444444444444" + filmResourceList.size());
            if (filmResourceList.size() > 0) {
                FilmResource filmResource = filmResourceList.get(0);
                resourceImageList = Arrays.asList(filmResource.getImages().split("#"));
                String image = resourceImageList.get(0);
                cameraFilmId = filmResource.getCameraFilmId();
                File file = new File(image);
                if (file.exists()) {
                    Glide.with(MineApp.sContext).asBitmap().load(image).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            MineApp.getApp().getFixedThreadPool().execute(() -> {
                                gpuImage.setImage(resource);
                                if (cameraFilmId == 1)
                                    gpuImage.setFilter(new GPUImageMonochromeFilter(0.57f, new float[]{0.6f, 0.45f, 0.3f, 1.f}));
                                else if (cameraFilmId == 2) {
                                    GPUImageColorBalanceFilter filter = new GPUImageColorBalanceFilter();
                                    filter.setMidtones(new float[]{0.46f, 0.23f, 0.15f});
                                    gpuImage.setFilter(filter);
                                } else if (cameraFilmId == 3)
                                    gpuImage.setFilter(new GPUImageContrastFilter(1.49f));
                                else
                                    gpuImage.setFilter(new GPUImageSaturationFilter(0.5f));
                                Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
                                File file1 = new File(image);
                                try {
                                    FileOutputStream os = new FileOutputStream(file1);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                                    os.flush();
                                    os.close();
                                    mPhotoManager.addFileUpload(0, file1);
                                } catch (Exception e) {
                                    FilmResourceDb filmResourceDb = new FilmResourceDb(Realm.getDefaultInstance());
                                    if (resourceImageList.size() == 1)
                                        filmResourceDb.deleteFilm(cameraFilmId);
                                    else
                                        filmResourceDb.updateImages(cameraFilmId, image, false);
                                    getGPUImage();
                                    e.printStackTrace();
                                }
                            });

                        }
                    });
                } else {
                    if (resourceImageList.size() == 1)
                        sFilmResourceDb.deleteFilm(cameraFilmId);
                    else
                        sFilmResourceDb.updateImages(cameraFilmId, image, false);
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
            }, MineApp.activity).setCameraFilmId(filmId).setImage(image);
            HttpManager.getInstance().doHttpDeal(api);
        }

    }


}