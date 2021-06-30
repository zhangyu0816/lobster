package com.zb.lib_base.db;

import android.text.TextUtils;

import com.zb.lib_base.model.FilmResource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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

    public List<FilmResource> getAllCameraFilm() {
        List<FilmResource> list = new ArrayList<>();
        beginTransaction();
        RealmResults<FilmResource> results = realm.where(FilmResource.class).findAll();
        if (results.size() > 0)
            list.addAll(results);
        commitTransaction();

        return list;
    }

    public FilmResource getCameraFilm(long cameraFilmId) {
        beginTransaction();
        FilmResource filmResource = realm.where(FilmResource.class).equalTo("cameraFilmId", cameraFilmId).findFirst();
        commitTransaction();
        return filmResource;
    }

    public void updateImages(long cameraFilmId, String images, boolean isAdd) {
        beginTransaction();
        FilmResource filmResource = realm.where(FilmResource.class).equalTo("cameraFilmId", cameraFilmId).findFirst();
        if (filmResource != null) {
            if (isAdd) {
                if (filmResource.getImages().isEmpty())
                    filmResource.setImages(images);
                else
                    filmResource.setImages(filmResource.getImages() + "#" + images);
            } else {
                String[] temps = filmResource.getImages().split("#");
                List<String> tempList = new ArrayList<>();
                for (String s : temps) {
                    tempList.add(s);
                }
                Iterator<String> iterator = tempList.iterator();
                while (iterator.hasNext()) {
                    String s = iterator.next();
                    if (TextUtils.equals(images, s)) {
                        iterator.remove();
                        break;
                    }
                }
                if (tempList.size() == 0)
                    filmResource.setImages("");
                else
                    filmResource.setImages(TextUtils.join("#", tempList));
            }
        }
        commitTransaction();
    }

    public void changeImages(long cameraFilmId, String images, String newUrl) {
        beginTransaction();
        FilmResource filmResource = realm.where(FilmResource.class).equalTo("cameraFilmId", cameraFilmId).findFirst();
        if (filmResource != null) {
            filmResource.setImages(filmResource.getImages().replace(images, newUrl));
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
