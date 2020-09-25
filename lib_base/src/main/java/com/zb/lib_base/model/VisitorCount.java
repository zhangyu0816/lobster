package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class VisitorCount extends BaseObservable {
    private int totalCount;//总
    private int manVisitorCount;//女
    private int womanVisitorCount; //男

    @Bindable
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        notifyPropertyChanged(BR.totalCount);
    }

    @Bindable
    public int getManVisitorCount() {
        return manVisitorCount;
    }

    public void setManVisitorCount(int manVisitorCount) {
        this.manVisitorCount = manVisitorCount;
        notifyPropertyChanged(BR.manVisitorCount);
    }

    @Bindable
    public int getWomanVisitorCount() {
        return womanVisitorCount;
    }

    public void setWomanVisitorCount(int womanVisitorCount) {
        this.womanVisitorCount = womanVisitorCount;
        notifyPropertyChanged(BR.womanVisitorCount);
    }
}
