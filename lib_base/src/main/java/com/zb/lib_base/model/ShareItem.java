package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ShareItem extends BaseObservable {
    private int shareRes;
    private String shareName="";

    public ShareItem() {
    }

    public ShareItem(int shareRes, String shareName) {
        this.shareRes = shareRes;
        this.shareName = shareName;
    }

    @Bindable
    public int getShareRes() {
        return shareRes;
    }

    public void setShareRes(int shareRes) {
        this.shareRes = shareRes;
        notifyPropertyChanged(BR.shareRes);
    }

    @Bindable public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
        notifyPropertyChanged(BR.shareName);
    }
}
