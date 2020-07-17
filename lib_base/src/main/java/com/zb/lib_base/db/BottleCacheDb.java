package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.BottleCache;

import io.realm.Realm;
import io.realm.RealmResults;

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
        BottleCache bottleCache = realm.where(BottleCache.class).equalTo("driftBottleId", driftBottleId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        commitTransaction();
        return bottleCache;
    }

    public void updateBottleCache(long driftBottleId, String image, String nick, CallBack callBack) {
        beginTransaction();
        BottleCache bottleCache = realm.where(BottleCache.class).equalTo("driftBottleId", driftBottleId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (bottleCache != null) {
            bottleCache.setImage(image);
            bottleCache.setNick(nick);
            bottleCache.setNoReadNum(0);
            callBack.success();
        } else {
            callBack.fail();
        }
        commitTransaction();
    }

    public interface CallBack {
        void success();

        void fail();
    }

    public void deleteBottleCache(long driftBottleId) {
        beginTransaction();
        BottleCache bottleCache = realm.where(BottleCache.class).equalTo("driftBottleId", driftBottleId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (bottleCache != null)
            bottleCache.deleteFromRealm();
        commitTransaction();
    }

    public int getUnReadCount() {
        int count = 0;
        beginTransaction();
        RealmResults<BottleCache> results = realm.where(BottleCache.class).equalTo("mainUserId", BaseActivity.userId).findAll();
        if (results.size() > 0) {
            for (BottleCache bottleCache : results) {
                count += bottleCache.getNoReadNum();
            }
        }
        commitTransaction();
        return count;
    }
}
