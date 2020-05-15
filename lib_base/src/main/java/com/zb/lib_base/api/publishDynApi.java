package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class publishDynApi extends BaseEntity<BaseResultEntity> {
    String text;     //文字
    String images;    //图片		多张图片用,分隔
    String videoUrl;  //视频地址
    int resTime; //资源时长  （视频，音频 时长  秒）
    String addressInfo;//完整地址信息
    String friendTitle;//

    public publishDynApi setText(String text) {
        this.text = text;
        return this;
    }

    public publishDynApi setImages(String images) {
        this.images = images;
        return this;
    }

    public publishDynApi setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    public publishDynApi setResTime(int resTime) {
        this.resTime = resTime;
        return this;
    }

    public publishDynApi setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
        return this;
    }

    public publishDynApi setFriendTitle(String friendTitle) {
        this.friendTitle = friendTitle;
        return this;
    }

    public publishDynApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("发布动态");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.publishDyn(text, images, videoUrl, resTime, 1, 0, 0, addressInfo, friendTitle);
    }
}
