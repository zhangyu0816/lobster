package com.zb.lib_base.model;

import com.zb.lib_base.activity.BaseActivity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatList extends RealmObject {


    @PrimaryKey
    long userId;//好友userId
    String nick = "";//好友昵称
    String image = "";//好友头像
    String creationDate = "";//会话时间
    String stanza = "";//内容
    int msgType;//消息类型 1：文字 2：图片 3：语音 4：视频  112：评论
    int noReadNum;//未读条数
    String publicTag = "";//标签   默认 null
    int effectType;//作用类型 1.无  2.系统内置      默认 null
    int authType; //权限类型  1.无限制  2.以下限制      默认 null
    long mainUserId = BaseActivity.userId;
    boolean hasNewBeLike = false;
    // 用户-> 该账号
    //不能发送红包
    //不能发视频
    //不能发起预约
    //订单信息  人气  不可见
    //租号不可见
    int chatType=0;// 1 喜欢我  2 漂流瓶  3超级喜欢  4 匹配成功

    long driftBottleId;	//所属漂流瓶

    public ChatList() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
    }

    public String getPublicTag() {
        return publicTag;
    }

    public void setPublicTag(String publicTag) {
        this.publicTag = publicTag;
    }

    public int getEffectType() {
        return effectType;
    }

    public void setEffectType(int effectType) {
        this.effectType = effectType;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(long mainUserId) {
        this.mainUserId = mainUserId;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
    }

    public boolean isHasNewBeLike() {
        return hasNewBeLike;
    }

    public void setHasNewBeLike(boolean hasNewBeLike) {
        this.hasNewBeLike = hasNewBeLike;
    }
}
