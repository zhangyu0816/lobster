package com.zb.lib_base.iv;

import android.view.View;

import androidx.databinding.ViewDataBinding;

@FunctionalInterface
public interface BaseVMInterface {

    void setBinding(ViewDataBinding binding);

    default void setAdapter() {
    }

    default void setList() {
    }

    default void back(View view){}
}
