package com.zb.lib_base.model;



import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class VipAd extends BaseObservable {
    private int topRes = 0;
    private int topBgRes = 0;
    private String title = "";
    private String content = "";

    @Bindable
    public int getTopRes() {
        return topRes;
    }

    public void setTopRes(int topRes) {
        this.topRes = topRes;
        notifyPropertyChanged(BR.topRes);
    }

    @Bindable public int getTopBgRes() {
        return topBgRes;
    }

    public void setTopBgRes(int topBgRes) {
        this.topBgRes = topBgRes;
        notifyPropertyChanged(BR.topBgRes);
    }

    @Bindable public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }
}
