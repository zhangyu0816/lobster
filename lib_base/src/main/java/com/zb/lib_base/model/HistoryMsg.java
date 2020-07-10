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

    int isDelete;         //状态 0：正常 1删除
    int isRead;           //状态 0：未读 1已读
    int msgChannelType = 1; //消息渠道类型  1.普通聊天 （默认）  2. 漂流瓶
    long driftBottleId = 0;    //所属漂流瓶
    int imPlatformType; //
    String thirdMessageId = ""; //第三方消息id

    boolean showTime = false;

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

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getMsgChannelType() {
        return msgChannelType;
    }

    public void setMsgChannelType(int msgChannelType) {
        this.msgChannelType = msgChannelType;
    }

    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
    }

    public int getImPlatformType() {
        return imPlatformType;
    }

    public void setImPlatformType(int imPlatformType) {
        this.imPlatformType = imPlatformType;
    }

    public String getThirdMessageId() {
        return thirdMessageId;
    }

    public void setThirdMessageId(String thirdMessageId) {
        this.thirdMessageId = thirdMessageId;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }
}
