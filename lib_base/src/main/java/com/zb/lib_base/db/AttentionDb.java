package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.AttentionInfo;

import io.realm.Realm;

/**
 * 关注
 */
public class AttentionDb extends BaseDao {
    public volatile static AttentionDb INSTANCE;

    public AttentionDb(Realm realm) {
        super(realm);
    }

    //获取单例
    public static AttentionDb getInstance() {
        if (INSTANCE == null) {
            synchronized (AttentionDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AttentionDb(Realm.getDefaultInstance());
                }
            }
        }
        return INSTANCE;
    }

    public void saveAttention(AttentionInfo attentionInfo) {
        beginTransaction();
        realm.insertOrUpdate(attentionInfo);
        commitTransaction();
    }

    public boolean isAttention(long otherUserId) {
        beginTransaction();
        AttentionInfo attentionInfo = realm.where(AttentionInfo.class).equalTo("otherUserId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        commitTransaction();
        return attentionInfo != null && attentionInfo.isAttention();
    }

    public AttentionInfo getAttentionInfo(long otherUserId) {
        beginTransaction();
        AttentionInfo attentionInfo = realm.where(AttentionInfo.class).equalTo("otherUserId", otherUserId).equalTo("mainUserId", BaseActivity.userId).findFirst();
        commitTransaction();
        return attentionInfo == null ? new AttentionInfo() : attentionInfo;
    }
}
