package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Reward extends BaseObservable {
    private long userId; // 评论者星号
    private String image = "";// 头像
    private String nick = "";// 昵称
    private double payMoney = 0.0;
    private int memberType;  //1免费用户   2 .会员
    private int faceAttest;  //人脸 V  0未认证  1认证

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
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
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable
    public double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(double payMoney) {
        this.payMoney = payMoney;
        notifyPropertyChanged(BR.payMoney);
    }

    @Bindable
    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
        notifyPropertyChanged(BR.memberType);
    }

    @Bindable
    public int getFaceAttest() {
        return faceAttest;
    }

    public void setFaceAttest(int faceAttest) {
        this.faceAttest = faceAttest;
        notifyPropertyChanged(BR.faceAttest);
    }
}
