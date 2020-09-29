package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.SystemMsg;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SystemMsgDb extends BaseDao {
    public volatile static SystemMsgDb INSTANCE;

    public SystemMsgDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static SystemMsgDb getInstance() {
        if (INSTANCE == null) {
            synchronized (SystemMsgDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SystemMsgDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveSystemMsg(SystemMsg systemMsg) {
        beginTransaction();
        realm.insertOrUpdate(systemMsg);
        commitTransaction();
    }

    public List<SystemMsg> getSystemMsgList() {
        beginTransaction();
        RealmResults<SystemMsg> results = realm.where(SystemMsg.class).equalTo("mainUserId", BaseActivity.userId).findAllSorted("creationDate", Sort.DESCENDING);
        commitTransaction();
        return results;
    }

    public void setShowTime(long id, boolean showTime) {
        beginTransaction();
        SystemMsg systemMsg = realm.where(SystemMsg.class).equalTo("id", id).equalTo("mainUserId", BaseActivity.userId).findFirst();
        if (systemMsg != null)
            systemMsg.setShowTime(showTime);
        commitTransaction();
    }
}
