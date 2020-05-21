package com.zb.lib_base.model;


import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * @author DIY
 * @date 2018-02-02
 */

public class AliPay extends BaseObservable {
    private String payInfo = "";

    @Bindable
    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
        notifyPropertyChanged(BR.payInfo);
    }
}
