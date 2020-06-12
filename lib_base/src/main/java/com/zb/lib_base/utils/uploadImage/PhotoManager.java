package com.zb.lib_base.utils.uploadImage;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.uploadImagesApi;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpUploadManager;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.utils.SCToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
import me.shaohui.advancedluban.OnMultiCompressListener;

public class PhotoManager {


    private RxAppCompatActivity context;
    private OnUpLoadImageListener listener;
    private boolean needProgress = true;

    public PhotoManager() {

    }

    public PhotoManager(RxAppCompatActivity context) {
        this.context = context;
        this.instantUpload = false;
    }

    public PhotoManager(RxAppCompatActivity context, boolean instantUpload) {
        this.context = context;
        this.instantUpload = instantUpload;
    }

    public PhotoManager(RxAppCompatActivity context, OnUpLoadImageListener listener) {
        this.context = context;
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


    public void setInstantUpload(boolean instantUpload) {
        this.instantUpload = instantUpload;
    }


    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }


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
     * @return
     */
    public PhotoManager addFile(File file) {
        addFile(-1, file, true);
        return this;
    }

    /**
     * 添加 文件  压缩 并上传
     *
     * @param index
     * @param file
     * @return
     */
    public PhotoManager addFile(int index, File file) {
        addFile(index, file, true);
        return this;
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
     * @param compress 是否压缩
     * @return
     */
    public PhotoManager addFile(final int index, File file, boolean compress) {
        if (null == file) {
            return this;
        }
        if (photos.size() >= maxSize) {
            return this;//不能继续添加
        }
        final String srcFilePath = file.getAbsolutePath();
        compressCount++;
        Luban.compress(context, file).putGear(Luban.THIRD_GEAR).launch(new OnCompressListener() {
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
                SCToastUtil.showToast(context, "压缩异常", true);
            }
        });
        return this;
    }

    /**
     * 添加文件 并立即上传
     *
     * @param index
     * @param file
     * @return
     */


    public PhotoManager addFileUpload(final int index, File file) {
        if (null == file) {
            return this;
        }
        final String srcFilePath = file.getAbsolutePath();
        compressCount = 0;
        Luban.compress(context, file).putGear(Luban.THIRD_GEAR).launch(new OnCompressListener() {
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
        return this;
    }

    public PhotoManager addFiles(List<String> filePaths, final CompressOver compressOver) {
        if (null == filePaths || filePaths.size() == 0) {
            return this;
        }
        if (photos.size() + filePaths.size() >= maxSize) {
            return this;//不能继续添加
        }
        compressOverBack = compressOver;
        compressCount = compressCount + filePaths.size();
        final List<File> files = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            files.add(new File(filePaths.get(i)));
        }
        Luban.compress(context, files).putGear(Luban.THIRD_GEAR).launch(new OnMultiCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(List<File> fileList) {
                for (int i = 0; i < fileList.size(); i++) {
                    Log.d("压缩成功", files.get(i).getAbsolutePath() + "/" + fileList.get(i).getAbsolutePath());
                    PhotoFile photoFile = new PhotoFile(files.get(i).getAbsolutePath(), fileList.get(i));
                    photos.add(photoFile);
                    if (instantUpload) {
                        needProgress = false;
                        uploadImage(photoFile);
                    }
                    compressCount--;
                    isCompress = false;
                    if (i == fileList.size() - 1)
                        new Thread(() -> {
                            try {
                                Thread.sleep(100);
                                handler.sendEmptyMessage(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                }
            }

            @Override
            public void onError(Throwable e) {
                SCToastUtil.showToast(context, "压缩异常", true);
            }
        });
        return this;
    }

    private CompressOver compressOverBack;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    compressOverBack.success();
                    break;
            }
            return false;
        }
    });

    public interface CompressOver {
        void success();
    }

    public PhotoManager addFiles(List<String> filePaths) {
        if (null == filePaths || filePaths.size() == 0) {
            return this;
        }
        if (photos.size() + filePaths.size() >= maxSize) {
            return this;//不能继续添加
        }
        compressCount = compressCount + filePaths.size();
        final List<File> files = new ArrayList<>();
        for (int i = 0; i < filePaths.size(); i++) {
            files.add(new File(filePaths.get(i)));
        }
        Luban.compress(context, files).putGear(Luban.THIRD_GEAR).launch(new OnMultiCompressListener() {
            @Override
            public void onStart() {
                isCompress = true;
            }

            @Override
            public void onSuccess(List<File> fileList) {
                for (int i = 0; i < fileList.size(); i++) {
                    Log.d("压缩成功", files.get(i).getAbsolutePath() + "/" + fileList.get(i).getAbsolutePath());
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
                SCToastUtil.showToast(context, "压缩异常", true);
            }
        });
        return this;
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
     * @return
     */
    public PhotoManager deleteSrcFile(String file) {
        if (null == file) {
            return this;
        }
        if (photos.size() <= 0) {
            return this;//不能删除了
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
        return this;
    }


    public PhotoManager deleteAllFile() {
        for (PhotoFile file : photos) {
            if (file.getPhotoeFile() != null) file.getPhotoeFile().deleteOnExit();
        }
        photos.clear();
        statisticsUploadStatus();
        return this;
    }

    /**
     * 拼接 全部的 webUrl
     *
     * @param separator 分隔符
     * @return
     */
    public String jointWebUrl(String separator) {
        String allWebUrl = "";
        for (int i = 0; i < photos.size(); i++) {
            PhotoFile photoFile = photos.get(i);
            if (photoFile.getUploadStatus() == 3) {
                allWebUrl += "" + photoFile.getWebUrl() + separator;
            }
        }
        if (allWebUrl.length() > 1) {
            allWebUrl = allWebUrl.substring(0, allWebUrl.length() - separator.length());
        }
        return allWebUrl;
    }


    //上传之前检查一次，图片上传的情况。(未成功的再上传一次,还不成功。返回出去。) 可根据方法自己写

    /**
     * 统计状态
     */
    public synchronized PhotoManager statisticsUploadStatus() {
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
        return this;
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
            SCToastUtil.showToast(context, "图片正在压缩...", true);
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


    public void uploadImage(final PhotoFile photoFile) {
        photoFile.setUploadStatus(2);
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
                photoFile.setUploadStatus(4);
                statisticsUploadStatus();
                if (reUploadCount <= maxReUpload) reUploadByFail();
                else if (listener != null) listener.onError(photoFile);
                else super.onError(e);
            }
        }, context)
                .setFile(photoFile.getPhotoeFile())
                .setIsCompre(2)
                .setIsCutImage(1);
        api.setShowProgress(needProgress);
        HttpUploadManager.getInstance().doHttpDeal(api);
    }

    @FunctionalInterface
    public interface OnUpLoadImageListener {
        void onSuccess();

        default void onError(PhotoFile file) {
        }
    }
}
