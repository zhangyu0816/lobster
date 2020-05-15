package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.CollectID;

import io.realm.Realm;

public class CollectIDDb extends BaseDao {

    public CollectIDDb(Realm realm) {
        super(realm);
    }

    public void saveCollectID(CollectID collectID) {
        beginTransaction();
        realm.insertOrUpdate(collectID);
        commitTransaction();
    }

    public boolean getCollectId(int type, long collectId) {
        beginTransaction();
        CollectID collectID = realm.where(CollectID.class).equalTo("collectId", collectId).equalTo("type", type).equalTo("userId", BaseActivity.userId).findFirst();
        commitTransaction();
        return collectID != null;
    }
}
