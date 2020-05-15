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

public class uploadSoundApi extends BaseEntity<ResourceUrl> {
    File file;

    public uploadSoundApi setFile(File file) {
        this.file = file;
        return this;
    }

    public uploadSoundApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("正在上传音频");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mp3"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return methods.uploadSound(RequestBody.create(MediaType.parse("multipart/form-data"), file.getName()), body);
    }
}
