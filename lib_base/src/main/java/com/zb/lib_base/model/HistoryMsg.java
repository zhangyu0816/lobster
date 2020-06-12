package com.zb.lib_base.model;

import com.zb.lib_base.activity.BaseActivity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HistoryMsg extends RealmObject {
    @PrimaryKey
    long id;               //id
    long fromId;             //发送人ID
    long toId;               //接收人ID
    String creationDate = "";     //发送时间
    String stanza = "";        //消息内容
    int msgType;         //消息类型 1：文字 2：图片 3：语音 4：视频
    String title = "";         //标题
    String resLink = "";         //资源链接
    int resTime;       //资源时长  秒
    long mainUserId = BaseActivity.userId;
    long otherUserId = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
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

    public long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(long mainUserId) {
        this.mainUserId = mainUserId;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }
}
