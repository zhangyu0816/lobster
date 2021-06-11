package com.zb.lib_base.db;

import com.zb.lib_base.model.FilmResource;

import io.realm.Realm;

public class FilmResourceDb extends BaseDao {
    public volatile static FilmResourceDb INSTANCE;

    public FilmResourceDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static FilmResourceDb getInstance() {
        if (INSTANCE == null) {
            synchronized (FilmResourceDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FilmResourceDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveData(FilmResource filmResource) {
        beginTransaction();
        realm.insertOrUpdate(filmResource);
        commitTransaction();
    }

    public FilmResource getCameraFilm(long cameraFilmId) {
        beginTransaction();
        FilmResource filmResource = realm.where(FilmResource.class).equalTo("cameraFilmId", cameraFilmId).findFirst();
        commitTransaction();
        return filmResource;
    }

    public void updateImages(long cameraFilmId, String images) {
        beginTransaction();
        FilmResource filmResource = realm.where(FilmResource.class).equalTo("cameraFilmId", cameraFilmId).findFirst();
        if (filmResource != null) {
            if (filmResource.getImages().isEmpty())
                filmResource.setImages(images);
            else
                filmResource.setImages(filmResource.getImages() + "#" + images);
        }
        commitTransaction();
    }

    public int getImageSize(long cameraFilmId) {
        FilmResource filmResource = getCameraFilm(cameraFilmId);
        int size = 0;
        if (filmResource != null && !filmResource.getImages().isEmpty()) {
            size = filmResource.getImages().split("#").length;
        }
        return size;
    }

    public void deleteFilm(long cameraFilmId) {
        beginTransaction();
        FilmResource filmResource = realm.where(FilmResource.class).equalTo("cameraFilmId", cameraFilmId).findFirst();
        if (filmResource != null) {
            filmResource.deleteFromRealm();
        }
        commitTransaction();
    }
}
