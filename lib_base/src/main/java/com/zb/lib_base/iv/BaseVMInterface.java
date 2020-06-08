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

    default void more(View view) {
    }

    default void follow(View view) {
    }

    default void superLike(View view) {
    }

    default void question(View view) {
    }

    default void selectPosition(int position) {
    }

    /********************** 公用网络请求 ***************************/

    default void myInfo() {
    }
}
