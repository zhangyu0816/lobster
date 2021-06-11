package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FilmResource extends RealmObject {
    @PrimaryKey
    long cameraFilmId;
    String images = "";

    public FilmResource() {
    }

    public FilmResource(long cameraFilmId, String images) {
        this.cameraFilmId = cameraFilmId;
        this.images = images;
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
}
