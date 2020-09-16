package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.CollectID;

import io.realm.Realm;

/**
 * 喜欢对方
 */
public class LikeDb extends BaseDao {
    public volatile static LikeDb INSTANCE;

    public LikeDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static LikeDb getInstance() {
        if (INSTANCE == null) {
            synchronized (LikeDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LikeDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveLike(CollectID collectID) {
        beginTransaction();
        realm.insertOrUpdate(collectID);
        commitTransaction();
    }

    public boolean hasLike(long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("userId", BaseActivity.userId).findFirst();
        commitTransaction();
        return collectID != null;
    }

    public void deleteLike(long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("userId", BaseActivity.userId).findFirst();
        if (collectID != null)
            collectID.deleteFromRealm();
        commitTransaction();
    }
}
