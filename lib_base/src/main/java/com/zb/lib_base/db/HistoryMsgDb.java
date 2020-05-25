package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.HistoryMsg;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class HistoryMsgDb extends BaseDao {

    public HistoryMsgDb(Realm realm) {
        super(realm);
    }

    public void saveHistoryMsg(HistoryMsg historyMsg) {
        beginTransaction();
        historyMsg.setMainUserId(BaseActivity.userId);
        realm.insertOrUpdate(historyMsg);
        commitTransaction();
    }

    public RealmResults<HistoryMsg> getRealmResults() {
        beginTransaction();
        RealmResults<HistoryMsg> results = realm.where(HistoryMsg.class).equalTo("mainUserId", BaseActivity.userId).findAllSorted("creationDate", Sort.DESCENDING);
        commitTransaction();
        return results;
    }
}
