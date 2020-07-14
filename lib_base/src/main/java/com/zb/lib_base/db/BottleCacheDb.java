package com.zb.lib_base.db;

import com.zb.lib_base.model.BottleCache;

import io.realm.Realm;

public class BottleCacheDb extends BaseDao {

    public BottleCacheDb(Realm realm) {
        super(realm);
    }

    public void saveBottleCache(BottleCache bottleCache) {
        beginTransaction();
        realm.insertOrUpdate(bottleCache);
        commitTransaction();
    }
    // 单个漂流瓶的最新聊天记录
    public BottleCache getBottleCache(long driftBottleId) {
        beginTransaction();
        BottleCache bottleCache = realm.where(BottleCache.class).equalTo("driftBottleId", driftBottleId).findFirst();
        commitTransaction();
        return bottleCache;
    }

    public void updateReadNum(long driftBottleId) {
        beginTransaction();
        BottleCache bottleCache = realm.where(BottleCache.class).equalTo("driftBottleId", driftBottleId).findFirst();
        if (bottleCache != null) {
            bottleCache.setNoReadNum(0);
        }
        commitTransaction();
    }

    public void deleteBottleCache(long driftBottleId) {
        beginTransaction();
        BottleCache bottleCache = realm.where(BottleCache.class).equalTo("driftBottleId", driftBottleId).findFirst();
        if (bottleCache != null)
            bottleCache.deleteFromRealm();
        commitTransaction();
    }
}
