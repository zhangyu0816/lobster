package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ShareInfo extends BaseObservable {
    private int sharetextId; // 内容id
    private String text = ""; // 内容

    @Bindable
    public int getSharetextId() {
        return sharetextId;
    }

    public void setSharetextId(int sharetextId) {
        this.sharetextId = sharetextId;
        notifyPropertyChanged(BR.sharetextId);
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
}
