package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.CollectID;

import io.realm.Realm;

/**
 * 动态点赞
 */
public class GoodDb extends BaseDao {
    public volatile static GoodDb INSTANCE;

    public GoodDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static GoodDb getInstance() {
        if (INSTANCE == null) {
            synchronized (GoodDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GoodDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveGood(CollectID collectID) {
        beginTransaction();
        realm.insertOrUpdate(collectID);
        commitTransaction();
    }

    public boolean hasGood(long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("userId", BaseActivity.userId).findFirst();
        commitTransaction();
        return collectID != null;
    }

    public void deleteGood(long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("userId", BaseActivity.userId).findFirst();
        if (collectID != null)
            collectID.deleteFromRealm();
        commitTransaction();
    }
}
