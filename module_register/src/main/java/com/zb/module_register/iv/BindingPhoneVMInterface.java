package com.zb.module_register.iv;

import android.view.View;

import com.zb.lib_base.model.ImageCaptcha;

public interface BindingPhoneVMInterface {

    void getCode(View view);

    void binding(View view);

    void banderCaptcha(ImageCaptcha imageCaptcha, String code);

    void bindingPhone();
}
