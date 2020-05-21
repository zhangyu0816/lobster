package com.zb.lib_base.model;



import com.zb.lib_base.BR;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class BottleInfo extends BaseObservable {
    long driftBottleId;//漂流瓶id
    long userId;
    String text = ""; //内容
    int driftBottleType; //漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
    long otherUserId; //拾起人id
    int noReadNum;//未读数量
    String headImage = "";//头像
    String nick = "";  //昵称
    String createTime = "2020-04-20 14:44:00";
    private List<BottleMsg> messageList = new ArrayList<>();

    @Bindable
    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        notifyPropertyChanged(BR.driftBottleId);
    }

    @Bindable public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable public int getDriftBottleType() {
        return driftBottleType;
    }

    public void setDriftBottleType(int driftBottleType) {
        this.driftBottleType = driftBottleType;
        notifyPropertyChanged(BR.driftBottleType);
    }

    @Bindable public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
        notifyPropertyChanged(BR.noReadNum);
    }

    @Bindable public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
        notifyPropertyChanged(BR.headImage);
    }

    @Bindable public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable public List<BottleMsg> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<BottleMsg> messageList) {
        this.messageList = messageList;
        notifyPropertyChanged(BR.messageList);
    }
}
