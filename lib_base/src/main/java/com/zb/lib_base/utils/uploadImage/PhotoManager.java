package com.zb.lib_base.utils.uploadImage;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.uploadImagesApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpChatUploadManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpUploadManager;
import com.zb.lib_base.http.UploadImageHelper;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.utils.SCToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
import me.shaohui.advancedluban.OnMultiCompressListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoManager {


    private OnUpLoadImageListener listener;
    private boolean needProgress = true;

    public PhotoManager() {

    }

    public PhotoManager(RxAppCompatActivity context, OnUpLoadImageListener listener) {
        this.instantUpload = false;
        this.listener = listener;
    }


    private volatile List<PhotoFile> photos = new CopyOnWriteArrayList<>();
    private boolean instantUpload = false;//是否立即上传
    private int maxSize = 30;//最大几张图
    private int maxReUpload = 3;//最少几张图

    private int unUploadSize = 0;//未上传
    private int currentUploadSize = 0;//正在上传的
    private int failSize = 0;//失败的
    private int successSize = 0;//成功的
    private int reUploadCount = 0;


    public int getImageSize() {
        return photos.size();
    }


    public PhotoManager addNotUpLoadFile(String url) {
        if (photos.size() >= maxSize) {
            return this;//不能继续添加
            //throw new Exception("不能继续添加");
        }
        photos.add(new PhotoFile(url));
        statisticsUploadStatus();
        return this;
    }

    public String getWebUrl(String srcFilePath) {
        String webUrl = "";
        for (PhotoFile photoFile : photos) {
            if (TextUtils.equals(photoFile.getSrcFilePath(), srcFilePath)) {
                webUrl = photoFile.getWebUrl();
                break;
            }
        }
        return webUrl;
    }

    /**
     * 添加 文件  压缩 并上传
     *
     * @param file
     */
    public void addFile(File file) {
        addFile(-1, file);
    }

    private int compressCount = 0;

    public int getCompressCount() {
        return compressCount;
    }

    private boolean isCompress = false;

    public boolean isCompress() {
        return isCompress;
    }

    /**
     * 添加 文件 并上传
     *
     * @param index
     * @param file
     */
    public void addFile(final int index, File file) {
        if (null == file) {
            return;
        }
        if (photos.size() >= maxSize) {
            return;//不能继续添加
        }
        final String srcFilePath = file.getAbsolutePath();
        compressCount++;
        Luban.compress(MineApp.sContext, file).putGear(Luban.CUSTOM_GEAR).setMaxSize(8 * 1024).launch(new OnCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(File file) {
                PhotoFile photoFile = new PhotoFile(srcFilePath, file);
                if (index >= 0) {
                    photos.add(index, photoFile);
                } else {
                    photos.add(photoFile);
                }
                if (instantUpload) {
                    needProgress = false;
                    uploadImage(photoFile);
                }
                compressCount--;
                isCompress = false;
            }

            @Override
            public void onError(Throwable e) {
                SCToastUtil.showToast(MineApp.activity, "压缩异常", true);
            }
        });
    }

    /**
     * 添加文件 并立即上传
     *
     * @param index
     * @param file
     */
    public void addFileUpload(final int index, File file) {
        if (null == file) {
            return;
        }
        final String srcFilePath = file.getAbsolutePath();
        compressCount = 0;
        Luban.compress(MineApp.sContext, file).putGear(Luban.CUSTOM_GEAR).setMaxSize(8 * 1024).launch(new OnCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(File file) {
                PhotoFile photoFile = new PhotoFile(srcFilePath, file);
                if (index >= 0) {
                    photos.add(index, photoFile);
                } else {
                    photos.add(photoFile);
                }
                isCompress = false;
                uploadImage(photoFile);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 添加文件 并立即上传
     *
     * @param index
     * @param file
     */
    public void addFileUploadForFilm(final int index, File file, long cameraFilmId, int cameraFilmType) {
        if (null == file) {
            return;
        }
        final String srcFilePath = file.getAbsolutePath();
        compressCount = 0;
        Luban.compress(MineApp.sContext, file).putGear(Luban.CUSTOM_GEAR).setMaxSize(8 * 1024).launch(new OnCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(File file) {
                PhotoFile photoFile = new PhotoFile(srcFilePath, file, cameraFilmId, cameraFilmType);
                if (index >= 0) {
                    photos.add(index, photoFile);
                } else {
                    photos.add(photoFile);
                }
                isCompress = false;
                uploadImage(photoFile);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    public void addFiles(List<String> filePaths, final CompressOver compressOver) {
        if (null == filePaths || filePaths.size() == 0) {
            return;
        }
        if (photos.size() + filePaths.size() >= maxSize) {
            return;//不能继续添加
        }
        compressOverBack = compressOver;
        compressCount = compressCount + filePaths.size();
        final List<File> files = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            files.add(new File(filePaths.get(i)));
        }
        Luban.compress(MineApp.sContext, files).putGear(Luban.CUSTOM_GEAR).setMaxSize(8 * 1024).launch(new OnMultiCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(List<File> fileList) {
                for (int i = 0; i < fileList.size(); i++) {
                    PhotoFile photoFile = new PhotoFile(files.get(i).getAbsolutePath(), fileList.get(i));
                    photos.add(photoFile);
                    if (instantUpload) {
                        needProgress = false;
                        uploadImage(photoFile);
                    }
                    compressCount--;
                    isCompress = false;
                    if (i == fileList.size() - 1) {
                        Runnable ra = () -> {
                            SystemClock.sleep(100);
                            MineApp.activity.runOnUiThread(() -> {
                                if (compressOverBack != null) {
                                    compressOverBack.success();
                                }
                            });
                        };
                        MineApp.getApp().getFixedThreadPool().execute(ra);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                SCToastUtil.showToast(MineApp.activity, "压缩异常", true);
            }
        });
    }

    private CompressOver compressOverBack;

    public interface CompressOver {
        void success();
    }

    public void addFiles(List<String> filePaths) {
        if (null == filePaths || filePaths.size() == 0) {
            return;
        }
        if (photos.size() + filePaths.size() >= maxSize) {
            return;//不能继续添加
        }
        compressCount = compressCount + filePaths.size();
        final List<File> files = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            files.add(new File(filePaths.get(i)));
        }
        Luban.compress(MineApp.sContext, files).putGear(Luban.CUSTOM_GEAR).setMaxSize(8 * 1024).launch(new OnMultiCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(List<File> fileList) {
                for (int i = 0; i < fileList.size(); i++) {
                    PhotoFile photoFile = new PhotoFile(files.get(i).getAbsolutePath(), fileList.get(i));
                    photos.add(photoFile);
                    if (instantUpload) {
                        needProgress = false;
                        uploadImage(photoFile);
                    }
                    compressCount--;
                    isCompress = false;
                }
            }

            @Override
            public void onError(Throwable e) {
                SCToastUtil.showToast(MineApp.activity, "压缩异常", true);
            }
        });
    }

    /**
     * move 图片可以移动到某个位置
     *
     * @param index
     * @param file
     * @param isCopy
     * @return
     */
    public PhotoManager moveFile(int index, File file, boolean isCopy) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getFilePath().equals(file.getAbsolutePath())) {
                photos.add(index, photos.get(i));
                if (!isCopy) {//如果不是复制，需要删掉原来位置上的信息
                    photos.remove(i);
                }
            }
        }
        return this;
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public PhotoManager deleteFile(File file) {
        if (null == file) {
            return this;
        }
        if (photos.size() <= 0) {
            return this;//不能删除了
            //throw new Exception("不能删除了");
        }
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getFilePath().equals(file.getAbsolutePath())) {
                photos.remove(i);
            }
        }
        return this;
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public void deleteSrcFile(String file) {
        if (null == file) {
            return;
        }
        if (photos.size() <= 0) {
            return;//不能删除了
            //throw new Exception("不能删除了");
        }
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getSrcFilePath().equals(file)) {
                if (photos.get(i).getPhotoeFile() != null)
                    photos.get(i).getPhotoeFile().deleteOnExit();
                photos.remove(i);
            }
        }
        statisticsUploadStatus();
    }


    public void deleteAllFile() {
        for (PhotoFile file : photos) {
            if (file.getPhotoeFile() != null) file.getPhotoeFile().deleteOnExit();
        }
        photos.clear();
        statisticsUploadStatus();
    }

    /**
     * 拼接 全部的 webUrl
     *
     * @param separator 分隔符
     * @return
     */
    public String jointWebUrl(String separator) {
        StringBuilder allWebUrl = new StringBuilder();
        for (int i = 0; i < photos.size(); i++) {
            PhotoFile photoFile = photos.get(i);
            if (photoFile.getUploadStatus() == 3) {
                allWebUrl.append("").append(photoFile.getWebUrl()).append(separator);
            }
        }
        if (allWebUrl.length() > 1) {
            allWebUrl = new StringBuilder(allWebUrl.substring(0, allWebUrl.length() - separator.length()));
        }
        return allWebUrl.toString();
    }


    //上传之前检查一次，图片上传的情况。(未成功的再上传一次,还不成功。返回出去。) 可根据方法自己写

    /**
     * 统计状态
     */
    public synchronized void statisticsUploadStatus() {
        ////1 未上传  2.正在上传  3.上传成功  4.上传失败
        clearSize();
        for (int i = 0; i < photos.size(); i++) {
            PhotoFile photoFile = photos.get(i);
            switch (photoFile.getUploadStatus()) {
                case 1:
                    unUploadSize++;
                    break;
                case 2:
                    currentUploadSize++;
                    break;
                case 3:
                    successSize++;
                    break;
                case 4:
                    failSize++;
                    break;
                default:
                    break;
            }
        }
    }

    private void clearSize() {
        failSize = 0;
        successSize = 0;
        unUploadSize = 0;
        currentUploadSize = 0;

    }

    public List<PhotoFile> getPhotoFiles() {
        return this.photos;
    }


    /**
     * 得到状态
     *
     * @param uploadStatus
     * @return
     */
    public int getPhotoUploadStatus(int uploadStatus) {
        switch (uploadStatus) {
            case 1:
                return unUploadSize;
            case 2:
                return currentUploadSize;
            case 3:
                return successSize;
            case 4:
                return failSize;
            default:
                break;
        }
        return 0;
    }

    /**
     * 失败的重新上传。
     */
    public void reUploadByFail() {
        for (int i = 0; i < photos.size(); i++) {
            PhotoFile photoFile = photos.get(i);
            if (photoFile.getUploadStatus() == 4) {
                reUploadCount++;
                uploadImage(photoFile);
            }
        }
    }

    /**
     * 所有非成功的，强制重新上传
     */
    public void reUploadByUnSuccess() {
        if (isCompress) {
            SCToastUtil.showToast(MineApp.activity, "图片正在压缩...", true);
            return;
        }
        for (int i = 0; i < photos.size(); i++) {
            PhotoFile photoFile = photos.get(i);
            if (photoFile.getUploadStatus() != 3) {
                uploadImage(photoFile);
            }
        }
        if (getPhotoUploadStatus(3) == photos.size() && listener != null) {
            listener.onSuccess();
        }
    }

    private boolean isChat = false;
    private boolean isGPU = false;

    public void setChat(boolean isChat) {
        this.isChat = isChat;
    }

    public void setNeedProgress(boolean needProgress) {
        this.needProgress = needProgress;
    }

    public void setGPU(boolean GPU) {
        isGPU = GPU;
    }

    public void uploadImage(final PhotoFile photoFile) {
        photoFile.setUploadStatus(2);
        if (isGPU) {
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (photoFile.getPhotoeFile() != null) {
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), photoFile.getPhotoeFile());
                String filename = photoFile.getPhotoeFile().getName();
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                requestBody.addFormDataPart("file", filename, body)
                        .addFormDataPart("fileContentType", "image/jpeg")
                        .addFormDataPart("isCutImage", "1")
                        .addFormDataPart("isCompre", "2");
            }
            Request request = new Request.Builder()
                    .url("https://img.zuwo.la/YmUpload_image")
                    .post(requestBody.build()).build();

            UploadImageHelper.getInstance().builder.build().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("onError", e.toString());
                    photoFile.setUploadStatus(4);
                    statisticsUploadStatus();
                    if (reUploadCount <= maxReUpload) reUploadByFail();
                    else if (listener != null) listener.onError(photoFile);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        Log.e("ForegroundLiveService", "uploadUrlSuccess");
                        String json = response.body().string();
                        JSONObject object = new JSONObject(json);
                        JSONObject data = new JSONObject(object.optString("data"));
                        photoFile.setWebUrl(data.optString("url"));
                        photoFile.setUploadStatus(3);
                        photoFile.getPhotoeFile().deleteOnExit();
                        listener.onSuccess(photoFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            uploadImagesApi api = new uploadImagesApi(new HttpOnNextListener<ResourceUrl>() {
                @Override
                public void onNext(ResourceUrl resourceUrl) {
                    photoFile.setWebUrl(resourceUrl.getUrl());
                    photoFile.setUploadStatus(3);
                    photoFile.getPhotoeFile().deleteOnExit();
                    statisticsUploadStatus();
                    if (getPhotoUploadStatus(3) == photos.size() && listener != null) {
                        listener.onSuccess();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("onError", e.toString());
                    photoFile.setUploadStatus(4);
                    statisticsUploadStatus();
                    if (reUploadCount <= maxReUpload) reUploadByFail();
                    else if (listener != null) listener.onError(photoFile);
                    else super.onError(e);
                }
            }, MineApp.activity)
                    .setFile(photoFile.getPhotoeFile())
                    .setIsCompre(2)
                    .setIsCutImage(1);
            api.setShowProgress(needProgress);
            if (isChat)
                HttpChatUploadManager.getInstance().doHttpDeal(api);
            else
                HttpUploadManager.getInstance().doHttpDeal(api);
        }
    }

    @FunctionalInterface
    public interface OnUpLoadImageListener {
        void onSuccess();

        default void onSuccess(PhotoFile file) {
        }

        default void onError(PhotoFile file) {
        }
    }
}
