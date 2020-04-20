package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class BottleInfo extends BaseObservable {
    String text = ""; //内容
    int driftBottleType; //漂流瓶状态 .1.漂流中 2.被拾起 3.销毁
    long otherUserId; //拾起人id
    private List<BottleMsg> messageList = new ArrayList<>();

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable
    public int getDriftBottleType() {
        return driftBottleType;
    }

    public void setDriftBottleType(int driftBottleType) {
        this.driftBottleType = driftBottleType;
        notifyPropertyChanged(BR.driftBottleType);
    }

    @Bindable
    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable
    public List<BottleMsg> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<BottleMsg> messageList) {
        this.messageList = messageList;
        notifyPropertyChanged(BR.messageList);
    }
}
