package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BottleCache extends RealmObject {
    @PrimaryKey
    long driftBottleId;//漂流瓶id
    String modifyTime = "";
    String text = ""; //内容

    public BottleCache() {
    }

    public BottleCache(long driftBottleId, String modifyTime, String text) {
        this.driftBottleId = driftBottleId;
        this.modifyTime = modifyTime;
        this.text = text;
    }

    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
