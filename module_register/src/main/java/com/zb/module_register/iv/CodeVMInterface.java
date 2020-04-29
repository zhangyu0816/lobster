package com.zb.module_register.iv;

import android.view.View;

@FunctionalInterface
public interface CodeVMInterface {

    void reset(View view);

    default void sure(View view) {
    }

    default void loginByCaptcha() {
    }

    default void registerCaptcha() {
    }

    default void loginCaptchaApi() {

    }
}
