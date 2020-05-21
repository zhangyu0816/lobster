package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class comsubApi extends BaseEntity<BaseResultEntity> {
    long complainTypeId;
    long comUserId;
    String comText;
    String images;

    public comsubApi setComplainTypeId(long complainTypeId) {
        this.complainTypeId = complainTypeId;
        return this;
    }

    public comsubApi setComUserId(long comUserId) {
        this.comUserId = comUserId;
        return this;
    }

    public comsubApi setComText(String comText) {
        this.comText = comText;
        return this;
    }

    public comsubApi setImages(String images) {
        this.images = images;
        return this;
    }

    public comsubApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交举报信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.comsub(complainTypeId, comUserId, comText, images);
    }
}
