package com.zb.lib_base.model;

import com.zb.lib_base.activity.BaseActivity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SystemMsg extends RealmObject {
    @PrimaryKey
    long id;
    int msgType;       //消息类型 1：文字 2：图片 3：语音 4：视频 5:地图坐标
    String stanza = "";           //消息内容
    String title = "";          //标题
    String resLink = "";        //资源链接
    int resTime;        //资源时长  秒
    String creationDate = "";
    int noReadNum;//未读条数
    boolean showTime = false;
    long mainUserId = BaseActivity.userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResLink() {
        return resLink;
    }

    public void setResLink(String resLink) {
        this.resLink = resLink;
    }

    public int getResTime() {
        return resTime;
    }

    public void setResTime(int resTime) {
        this.resTime = resTime;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(long mainUserId) {
        this.mainUserId = mainUserId;
    }
}
