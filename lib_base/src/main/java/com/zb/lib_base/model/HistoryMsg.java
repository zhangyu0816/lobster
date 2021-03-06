package com.zb.lib_base.model;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.mimc.CustomMessageBody;
import com.zb.lib_base.utils.DateUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HistoryMsg extends RealmObject {
    @PrimaryKey
    String thirdMessageId = ""; //第三方消息id
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
    long flashTalkId = 0;    //所属漂流瓶
    int imPlatformType; //
        boolean showTime = false;
    String theChatUk = "";        //两个人的Id拼接起来，小的在前面  #12#101#

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

    public String getTheChatUk() {
        return theChatUk;
    }

    public void setTheChatUk(String theChatUk) {
        this.theChatUk = theChatUk;
    }

    public long getFlashTalkId() {
        return flashTalkId;
    }

    public HistoryMsg setFlashTalkId(long flashTalkId) {
        this.flashTalkId = flashTalkId;
        return this;
    }

    public static HistoryMsg createHistory(String msgId, CustomMessageBody body, long otherUserId, int msgChannelType, long driftBottleId) {
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setThirdMessageId(msgId);
        historyMsg.setFromId(body.getFromId());
        historyMsg.setToId(body.getToId());
        historyMsg.setTitle(body.getSummary());
        historyMsg.setStanza(body.getStanza());
        historyMsg.setMsgType(body.getMsgType());
        historyMsg.setResLink(body.getResLink());
        historyMsg.setResTime(body.getResTime());
        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setMsgChannelType(msgChannelType);
        historyMsg.setDriftBottleId(driftBottleId);
        historyMsg.setMainUserId(BaseActivity.userId);
        return historyMsg;
    }

    public static HistoryMsg createHistoryForFlash(String msgId, CustomMessageBody body, long otherUserId, int msgChannelType, long flashTalkId) {
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setThirdMessageId(msgId);
        historyMsg.setFromId(body.getFromId());
        historyMsg.setToId(body.getToId());
        historyMsg.setTitle(body.getSummary());
        historyMsg.setStanza(body.getStanza());
        historyMsg.setMsgType(body.getMsgType());
        historyMsg.setResLink(body.getResLink());
        historyMsg.setResTime(body.getResTime());
        historyMsg.setCreationDate(DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss));
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setMsgChannelType(msgChannelType);
        historyMsg.setFlashTalkId(flashTalkId);
        historyMsg.setMainUserId(BaseActivity.userId);
        return historyMsg;
    }

    public static HistoryMsg createHistoryForPrivate(PrivateMsg privateMsg, long otherUserId, int msgChannelType, long driftBottleId) {
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setThirdMessageId(privateMsg.getThirdMessageId());
        historyMsg.setMainUserId(BaseActivity.userId);
        historyMsg.setFromId(privateMsg.getFromId());
        historyMsg.setToId(privateMsg.getToId());
        historyMsg.setCreationDate(privateMsg.getCreationDate());
        historyMsg.setStanza(privateMsg.getStanza());
        historyMsg.setMsgType(privateMsg.getMsgType());
        historyMsg.setTitle(privateMsg.getTitle());
        historyMsg.setResTime(privateMsg.getResTime());
        historyMsg.setResLink(privateMsg.getResLink());
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setMsgChannelType(msgChannelType);
        historyMsg.setDriftBottleId(driftBottleId);
        return historyMsg;
    }

    public static HistoryMsg createHistoryForPrivate(PrivateMsg privateMsg, long otherUserId, long flashTalkId) {
        HistoryMsg historyMsg = new HistoryMsg();
        historyMsg.setThirdMessageId(privateMsg.getThirdMessageId());
        historyMsg.setMainUserId(BaseActivity.userId);
        historyMsg.setFromId(privateMsg.getFromId());
        historyMsg.setToId(privateMsg.getToId());
        historyMsg.setCreationDate(privateMsg.getCreationDate());
        historyMsg.setStanza(privateMsg.getStanza());
        historyMsg.setMsgType(privateMsg.getMsgType());
        historyMsg.setTitle(privateMsg.getTitle());
        historyMsg.setResTime(privateMsg.getResTime());
        historyMsg.setResLink(privateMsg.getResLink());
        historyMsg.setOtherUserId(otherUserId);
        historyMsg.setMsgChannelType(3);
        historyMsg.setFlashTalkId(flashTalkId);
        return historyMsg;
    }
}
