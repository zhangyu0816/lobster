package com.zb.lib_base.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DIY on 2019-03-06.
 */

public class ResFile extends RealmObject {
    @PrimaryKey
    private String resLink = "";
    private String filePath = "";

    public ResFile() {
    }

    public ResFile(String resLink, String filePath) {
        this.resLink = resLink;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ResFile{" +
                "resLink='" + resLink + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getResLink() {
        return resLink;
    }

    public void setResLink(String resLink) {
        this.resLink = resLink;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
