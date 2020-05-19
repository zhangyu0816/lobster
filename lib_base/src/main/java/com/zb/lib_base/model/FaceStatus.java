package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FaceStatus extends BaseObservable {
    long userId;   //userId
    //用户上传上来的信息
    int isChecked;    //是否通过审核 //0未审核(无法提交)  //1通过(无法提交)  2未通过(显示不通过原因。备注)
    String mark = "";       //备注 ，退回的原因

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
        notifyPropertyChanged(BR.isChecked);
    }

    @Bindable
    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
        notifyPropertyChanged(BR.mark);
    }
}
