package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.ResourceUrl;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

public class uploadImagesApi extends BaseEntity<ResourceUrl> {
    private File file;
    private Integer isCompre;//1压缩(默认的) 2不压缩(备用字段)
    private Integer isCutImage;//系统  1.需要切(默认的) 2.不需切的图片(小部分)

    public uploadImagesApi setFile(File file) {
        this.file = file;
        return this;
    }

    public uploadImagesApi setIsCompre(Integer isCompre) {
        this.isCompre = isCompre;
        return this;
    }

    public uploadImagesApi setIsCutImage(Integer isCutImage) {
        this.isCutImage = isCutImage;
        return this;
    }

    public uploadImagesApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("正在上传图片....");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return methods.uploadImages(RequestBody.create(MediaType.parse("multipart/form-data"), isCompre + ""),
                RequestBody.create(MediaType.parse("multipart/form-data"), isCutImage + ""),
                RequestBody.create(MediaType.parse("multipart/form-data"), file.getName()),
                RequestBody.create(MediaType.parse("multipart/form-data"), "image/jpeg"),
                body);
    }
}