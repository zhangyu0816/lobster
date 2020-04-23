package com.zb.lib_base.model;

import io.realm.RealmObject;

public class Tag extends RealmObject {
    String name = "";
    String tags = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
