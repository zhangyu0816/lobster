package com.zb.module_register.iv;

import android.view.View;

@FunctionalInterface
public interface LoginVMInterface {

    void complete(View view);

    default void loginByPass() {
    }
}
