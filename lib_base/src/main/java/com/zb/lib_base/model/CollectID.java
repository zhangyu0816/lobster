package com.zb.lib_base.model;

import io.realm.RealmObject;

public class CollectID extends RealmObject {
    private long collectId;
    private int type = 0;// 1 关注  2 喜欢/点赞  3 评论
    private long userId;

    public long getCollectId() {
        return collectId;
    }

    public void setCollectId(long collectId) {
        this.collectId = collectId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
