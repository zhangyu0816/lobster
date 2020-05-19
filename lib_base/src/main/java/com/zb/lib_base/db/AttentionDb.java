package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.CollectID;

import io.realm.Realm;

/**
 * 点赞
 */
public class AttentionDb extends BaseDao {

    public AttentionDb(Realm realm) {
        super(realm);
    }

    public void saveAttention(CollectID collectID) {
        beginTransaction();
        realm.insertOrUpdate(collectID);
        commitTransaction();
    }

    public boolean hasAttention(long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("userId", BaseActivity.userId).findFirst();
        commitTransaction();
        return collectID != null;
    }

    public void deleteAttention(long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("userId", BaseActivity.userId).findFirst();
        if (collectID != null)
            collectID.deleteFromRealm();
        commitTransaction();
    }
}
