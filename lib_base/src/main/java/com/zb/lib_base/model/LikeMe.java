package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class LikeMe extends BaseObservable {
    long otherUserId ;//对方用户的id
    int likeOtherStatus;// 0 不喜欢  1 喜欢  2.超级喜欢
    String nick=""; //昵称
    String headImage="" ;// 头像
    int idAttest; //实名认证 0未认证  1认证
    String modifyTime="";

    @Bindable
    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable public int getLikeOtherStatus() {
        return likeOtherStatus;
    }

    public void setLikeOtherStatus(int likeOtherStatus) {
        this.likeOtherStatus = likeOtherStatus;
        notifyPropertyChanged(BR.likeOtherStatus);
    }

    @Bindable public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
        notifyPropertyChanged(BR.headImage);
    }

    @Bindable public int getIdAttest() {
        return idAttest;
    }

    public void setIdAttest(int idAttest) {
        this.idAttest = idAttest;
        notifyPropertyChanged(BR.idAttest);
    }

    @Bindable public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
        notifyPropertyChanged(BR.modifyTime);
    }
}
