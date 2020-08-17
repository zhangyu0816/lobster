package com.zb.module_register.iv;

import android.view.View;

import com.zb.lib_base.model.RegisterInfo;

@FunctionalInterface
public interface ImagesVMInterface {

    void complete(View view);

    default void selectImage(int position) {

    }

    default void register(RegisterInfo registerInfo) {
    }

    default void loginByUnion(RegisterInfo registerInfo) {
    }
}
