package com.zb.lib_base.model;

import com.zb.lib_base.activity.BaseActivity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CollectID extends RealmObject {
    @PrimaryKey
    private long collectId;
    private long userId;

    public CollectID(long collectId) {
        this.collectId = collectId;
        this.userId = BaseActivity.userId;
    }

    public long getCollectId() {
        return collectId;
    }

    public void setCollectId(long collectId) {
        this.collectId = collectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
