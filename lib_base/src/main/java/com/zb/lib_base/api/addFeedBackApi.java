package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class addFeedBackApi extends BaseEntity<BaseResultEntity> {
    String content;
    String images;
    String title;

    public addFeedBackApi setContent(String content) {
        this.content = content;
        return this;
    }

    public addFeedBackApi setImages(String images) {
        this.images = images;
        return this;
    }

    public addFeedBackApi setTitle(String title) {
        this.title = title;
        return this;
    }

    public addFeedBackApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交反馈信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.addFeedBack(title, content, images);
    }
}
