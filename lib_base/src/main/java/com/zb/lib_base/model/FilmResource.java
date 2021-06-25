package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FilmResource extends RealmObject {
    @PrimaryKey
    long cameraFilmId;
    int cameraFilmType;//胶卷类型
    String images = "";// 胶卷本地图片地址集合

    public FilmResource() {
    }

    public FilmResource(long cameraFilmId, String images, int cameraFilmType) {
        this.cameraFilmId = cameraFilmId;
        this.images = images;
        this.cameraFilmType = cameraFilmType;
    }

    public long getCameraFilmId() {
        return cameraFilmId;
    }

    public void setCameraFilmId(long cameraFilmId) {
        this.cameraFilmId = cameraFilmId;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getCameraFilmType() {
        return cameraFilmType;
    }

    public void setCameraFilmType(int cameraFilmType) {
        this.cameraFilmType = cameraFilmType;
    }

}
