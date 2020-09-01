package com.zb.lib_base.model;

import com.zb.lib_base.activity.BaseActivity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LikeType extends RealmObject {
    @PrimaryKey
    private long otherUserId;
    private int type = 0; // 1：喜欢  2：超级喜欢
    private long mainUserId;

    public LikeType() {
    }

    public LikeType(long otherUserId, int type) {
        this.otherUserId = otherUserId;
        this.type = type;
        this.mainUserId = BaseActivity.userId;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(long mainUserId) {
        this.mainUserId = mainUserId;
    }
}
