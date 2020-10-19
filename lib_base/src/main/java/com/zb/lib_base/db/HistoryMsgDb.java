package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.HistoryMsg;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class HistoryMsgDb extends BaseDao {
    public volatile static HistoryMsgDb INSTANCE;

    public HistoryMsgDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static HistoryMsgDb getInstance() {
        if (INSTANCE == null) {
            synchronized (HistoryMsgDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HistoryMsgDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveHistoryMsg(HistoryMsg historyMsg) {
        beginTransaction();
        historyMsg.setMainUserId(BaseActivity.userId);
        realm.insertOrUpdate(historyMsg);
        commitTransaction();
    }

    public RealmResults<HistoryMsg> getRealmResults(long otherUserId, int msgChannelType, long driftBottleId, long flashTalkId) {
        beginTransaction();
        RealmResults<HistoryMsg> results = realm.where(HistoryMsg.class).
                equalTo("otherUserId", otherUserId).
                equalTo("msgChannelType", msgChannelType).
                equalTo("driftBottleId", driftBottleId).
                equalTo("flashTalkId", flashTalkId).
                equalTo("mainUserId", BaseActivity.userId).
                findAllSorted("creationDate", Sort.DESCENDING);
        commitTransaction();
        return results;
    }

    public void deleteHistoryMsg(long otherUserId, int msgChannelType, long driftBottleId, long flashTalkId) {
        beginTransaction();
        RealmResults<HistoryMsg> results = realm.where(HistoryMsg.class).
                equalTo("otherUserId", otherUserId).
                equalTo("msgChannelType", msgChannelType).
                equalTo("driftBottleId", driftBottleId).
                equalTo("flashTalkId", flashTalkId).
                equalTo("mainUserId", BaseActivity.userId).
                findAll();
        if (results.size() > 0) {
            results.deleteAllFromRealm();
        }
        commitTransaction();
    }

    public int getMyChatCount(long otherUserId,  long flashTalkId){
        beginTransaction();
        RealmResults<HistoryMsg> results =  realm.where(HistoryMsg.class). equalTo("otherUserId", otherUserId).
                equalTo("msgChannelType", 3).
                equalTo("flashTalkId", flashTalkId).
                equalTo("fromId", BaseActivity.userId).
                equalTo("mainUserId", BaseActivity.userId).findAllSorted("creationDate", Sort.DESCENDING);
        commitTransaction();

        return results.size();
    }

    public int getOtherChatCount(long otherUserId,  long flashTalkId){
        beginTransaction();
        RealmResults<HistoryMsg> results =  realm.where(HistoryMsg.class). equalTo("otherUserId", otherUserId).
                equalTo("msgChannelType", 3).
                equalTo("flashTalkId", flashTalkId).
                equalTo("fromId", otherUserId).
                equalTo("mainUserId", BaseActivity.userId).findAllSorted("creationDate", Sort.DESCENDING);
        commitTransaction();
        return results.size();
    }
}
