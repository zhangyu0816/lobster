package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class BottleNoRead extends BaseObservable {
    int noReadNum;

    @Bindable public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
        notifyPropertyChanged(BR.noReadNum);
    }
}
