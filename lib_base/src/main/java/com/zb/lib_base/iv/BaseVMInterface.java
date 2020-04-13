package com.zb.lib_base.iv;

import android.view.View;

import androidx.databinding.ViewDataBinding;

@FunctionalInterface
public interface BaseVMInterface {

    void setBinding(ViewDataBinding binding);

    default void setAdapter() {
    }

    default void back(View view) {
    }

    default void right(View view) {
    }

    default void clickItem(int position) {
    }
}
