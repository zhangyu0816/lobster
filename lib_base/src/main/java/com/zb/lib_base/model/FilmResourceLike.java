package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FilmResourceLike extends RealmObject {
    long ownUserId;
    @PrimaryKey
    long cameraFilmResourceId;

    public long getOwnUserId() {
        return ownUserId;
    }

    public void setOwnUserId(long ownUserId) {
        this.ownUserId = ownUserId;
    }

    public long getCameraFilmResourceId() {
        return cameraFilmResourceId;
    }

    public void setCameraFilmResourceId(long cameraFilmResourceId) {
        this.cameraFilmResourceId = cameraFilmResourceId;
    }
}
