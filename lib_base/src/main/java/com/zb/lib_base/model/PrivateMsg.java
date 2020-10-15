package com.zb.lib_base.model;

public class PrivateMsg {

    private long id;
    private long fromId; // 发送人ID
    private long toId; // 接收人ID
    private String creationDate = ""; // 发送时间
    private String stanza = ""; // 消息内容
    private int msgType; // 消息类型 1：文字 2：图片 3：语音 4：视频
    private String title = ""; // 标题
    private String resLink = ""; // 资源链接
    private int resTime; // 资源时长 秒
    // 新增
    private int imPlatformType; //1.zuwoIM 2.阿里OpenIM 当前使用：2
    private String thirdMessageId = ""; //第三方消息id

    private int isDelete;         //状态 0：正常 1删除
    private int isRead;           //状态 0：未读 1已读
    private int msgChannelType = 1; //消息渠道类型  1.普通聊天 （默认）  2. 漂流瓶
    private long driftBottleId = 0;    //所属漂流瓶

    private long flashTalkId;    //所属闪聊

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

    public long getFlashTalkId() {
        return flashTalkId;
    }

    public void setFlashTalkId(long flashTalkId) {
        this.flashTalkId = flashTalkId;
    }
}
