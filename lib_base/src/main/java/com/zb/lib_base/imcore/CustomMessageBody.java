package com.zb.lib_base.imcore;

import com.alibaba.mobileim.conversation.YWMessageBody;

/**
 * Created by DIY on 2019-03-12.
 */

public class CustomMessageBody extends YWMessageBody {

    // 通用
    private long mFromId;
    private long mToId;
    // 消息
    private int mMsgType;
    private String mStanza;
    private String mResLink;
    private int mResTime;
    private String mSummary;
    // 好友
    private int status;
    private String askMark;

    public CustomMessageBody(int msgType, String stanza, String resLink,
                             int resTime, long fromId, long toId, String summary) {
        mMsgType = msgType;
        mStanza = stanza;
        mResLink = resLink;
        mResTime = resTime;
        mSummary = summary;
        mFromId = fromId;
        mToId = toId;
    }

    public CustomMessageBody(long fromId, long toId, String summary,
                             int status, String askMark) {
        mSummary = summary;
        mFromId = fromId;
        mToId = toId;
        this.status = status;
        this.askMark = askMark;
    }

    public CustomMessageBody() {
    }

    public int getMsgType() {
        return mMsgType;
    }

    public void setMsgType(int mMsgType) {
        this.mMsgType = mMsgType;
    }

    public String getStanza() {
        return mStanza;
    }

    public void setStanza(String mStanza) {
        this.mStanza = mStanza;
    }

    public String getResLink() {
        return mResLink;
    }

    public void setResLink(String mResLink) {
        this.mResLink = mResLink;
    }

    public int getResTime() {
        return mResTime;
    }

    public void setResTime(int mResTime) {
        this.mResTime = mResTime;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public long getFromId() {
        return mFromId;
    }

    public void setFromId(long fromId) {
        this.mFromId = fromId;
    }

    public long getToId() {
        return mToId;
    }

    public void setToId(long toId) {
        this.mToId = toId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAskMark() {
        return askMark;
    }

    public void setAskMark(String askMark) {
        this.askMark = askMark;
    }
}
