package com.zb.lib_base.model;

import com.zb.lib_base.BR;
import com.zb.lib_base.activity.BaseActivity;

import androidx.databinding.Bindable;

public class ChatMsg extends NewBaseObservable {
    long userId;//好友userId
    String nick = "";//好友昵称
    String image = "";//好友头像
    String creationDate = "";//会话时间
    String stanza = "";//内容
    int msgType;//消息类型 1：文字 2：图片 3：语音 4：视频
    int noReadNum;//未读条数
    String publicTag = "";//标签   默认 null
    int effectType;//作用类型 1.无  2.系统内置      默认 null
    int authType; //权限类型  1.无限制  2.以下限制      默认 null
    long mainUserId = BaseActivity.userId;
    // 用户-> 该账号
    //不能发送红包
    //不能发视频
    //不能发起预约
    //订单信息  人气  不可见
    //租号不可见

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        notifyPropertyChanged(BR.creationDate);
    }

    @Bindable
    public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
        notifyPropertyChanged(BR.stanza);
    }

    @Bindable
    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
        notifyPropertyChanged(BR.msgType);
    }

    @Bindable
    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
        notifyPropertyChanged(BR.noReadNum);
    }

    @Bindable
    public String getPublicTag() {
        return publicTag;
    }

    public void setPublicTag(String publicTag) {
        this.publicTag = publicTag;
        notifyPropertyChanged(BR.publicTag);
    }

    @Bindable
    public int getEffectType() {
        return effectType;
    }

    public void setEffectType(int effectType) {
        this.effectType = effectType;
        notifyPropertyChanged(BR.effectType);
    }

    @Bindable
    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
        notifyPropertyChanged(BR.authType);
    }

    @Bindable
    public long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(long mainUserId) {
        this.mainUserId = mainUserId;
        notifyPropertyChanged(BR.mainUserId);
    }
}
