package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class saveCameraFilmApi extends BaseEntity<BaseResultEntity> {
    String title; //胶卷标题
    int camerafilmType;  //胶卷类型  你们自定义传上来
    int authority;  //权限设置 1 公开 2 仅好友可见 3.私密
    long cameraFilmId; //胶卷id

    public saveCameraFilmApi setTitle(String title) {
        this.title = title;
        return this;
    }

    public saveCameraFilmApi setCamerafilmType(int camerafilmType) {
        this.camerafilmType = camerafilmType;
        return this;
    }

    public saveCameraFilmApi setAuthority(int authority) {
        this.authority = authority;
        return this;
    }

    public saveCameraFilmApi setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
        return this;
    }

    public saveCameraFilmApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, Object> map = new HashMap<>();
        if (cameraFilmId != 0)
            map.put("cameraFilmId", cameraFilmId);
        map.put("title", title);
        map.put("camerafilmType", camerafilmType);
        map.put("authority", authority);
        return methods.saveCameraFilm(map);
    }
}
