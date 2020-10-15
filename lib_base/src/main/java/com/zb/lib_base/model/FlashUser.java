package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FlashUser extends BaseObservable {
    long flashTalkId;
    long userId;
    long otherUserId;
    long flashTalkForUserId;  //属于谁的闪聊  用户id

    @Bindable
    public long getFlashTalkId() {
        return flashTalkId;
    }

    public void setFlashTalkId(long flashTalkId) {
        this.flashTalkId = flashTalkId;
        notifyPropertyChanged(BR.flashTalkId);
    }

    @Bindable public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable public long getFlashTalkForUserId() {
        return flashTalkForUserId;
    }

    public void setFlashTalkForUserId(long flashTalkForUserId) {
        this.flashTalkForUserId = flashTalkForUserId;
        notifyPropertyChanged(BR.flashTalkForUserId);
    }
}
