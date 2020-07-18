package com.zb.lib_base.db;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.AttentionInfo;

import io.realm.Realm;

/**
 * 关注
 */
public class AttentionDb extends BaseDao {

    public AttentionDb(Realm realm) {
        super(realm);
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
