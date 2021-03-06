package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.LikeType;

import io.realm.Realm;

public class LikeTypeDb extends BaseDao {
    public volatile static LikeTypeDb INSTANCE;

    public LikeTypeDb(Realm realm) {
        super(realm);
    }

    //θ·εεδΎ
    public static LikeTypeDb getInstance() {
        if (INSTANCE == null) {
            synchronized (LikeTypeDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LikeTypeDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveLikeType(LikeType likeType) {
        beginTransaction();
        realm.insertOrUpdate(likeType);
        commitTransaction();
    }

    public int getType(long otherUserId) {
        beginTransaction();
        LikeType likeType = realm.where(LikeType.class).equalTo("otherUserId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        commitTransaction();
        return likeType == null ? 0 : likeType.getType();
    }

    public void setType(long otherUserId, int type) {
        beginTransaction();
        LikeType likeType = realm.where(LikeType.class).equalTo("otherUserId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (likeType != null)
            likeType.setType(type);
        else
            saveLikeType(new LikeType(otherUserId, type));
        commitTransaction();
    }

    public void deleteLikeType(long otherUserId) {
        beginTransaction();
        LikeType likeType = realm.where(LikeType.class).equalTo("otherUserId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (likeType != null)
            likeType.deleteFromRealm();
        commitTransaction();
    }
}
