package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class SystemMsg extends BaseObservable {
    long id;
    int msgType;       //消息类型 1：文字 2：图片 3：语音 4：视频 5:地图坐标
    String stanza = "";           //消息内容
    String title = "";          //标题
    String resLink = "";        //资源链接
    int resTime;        //资源时长  秒
    String creationDate = "";
    int noReadNum;//未读条数

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
        notifyPropertyChanged(BR.msgType);
    }

    @Bindable public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
        notifyPropertyChanged(BR.stanza);
    }

    @Bindable public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable public String getResLink() {
        return resLink;
    }

    public void setResLink(String resLink) {
        this.resLink = resLink;
        notifyPropertyChanged(BR.resLink);
    }

    @Bindable public int getResTime() {
        return resTime;
    }

    public void setResTime(int resTime) {
        this.resTime = resTime;
        notifyPropertyChanged(BR.resTime);
    }

    @Bindable public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        notifyPropertyChanged(BR.creationDate);
    }

    @Bindable public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
        notifyPropertyChanged(BR.noReadNum);
    }
}
