package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.FilmResourceLike;

import io.realm.Realm;

public class FilmResourceLikeDb extends BaseDao {
    public volatile static FilmResourceLikeDb INSTANCE;

    public FilmResourceLikeDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static FilmResourceLikeDb getInstance() {
        if (INSTANCE == null) {
            synchronized (FilmResourceLikeDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FilmResourceLikeDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveDate(long cameraFilmResourceId) {
        FilmResourceLike filmResourceLike = new FilmResourceLike();
        filmResourceLike.setOwnUserId(BaseActivity.userId);
        filmResourceLike.setCameraFilmResourceId(cameraFilmResourceId);
        beginTransaction();
        realm.insertOrUpdate(filmResourceLike);
        commitTransaction();
    }

    public boolean hasDate(long cameraFilmResourceId) {
        beginTransaction();
        FilmResourceLike result = realm.where(FilmResourceLike.class).equalTo("cameraFilmResourceId", cameraFilmResourceId).findFirst();
        commitTransaction();
        return result != null;
    }

    public void deleteDate(long cameraFilmResourceId) {
        beginTransaction();
        FilmResourceLike result = realm.where(FilmResourceLike.class).equalTo("cameraFilmResourceId", cameraFilmResourceId).findFirst();
        if (result != null)
            result.deleteFromRealm();
        commitTransaction();
    }
}
