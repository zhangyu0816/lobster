package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.MineInfo;

import io.realm.Realm;

public class MineInfoDb extends BaseDao {

    public MineInfoDb(Realm realm) {
        super(realm);
    }

    public void saveMineInfo(MineInfo mineInfo) {
        beginTransaction();
        realm.insertOrUpdate(mineInfo);
        commitTransaction();
    }

    public MineInfo getMineInfo() {
        beginTransaction();
        MineInfo mineInfo = realm.where(MineInfo.class).equalTo("userId", BaseActivity.userId).findFirst();
        commitTransaction();
        return mineInfo;
    }
}
