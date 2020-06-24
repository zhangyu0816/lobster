package com.zb.module_mine.iv;

import android.view.View;

import com.zb.lib_base.model.ImageCaptcha;

public interface ModifyPassVMInterface {

    void modify(View view);

    void changeType(View view);

    void getCode(View view);

    void findPassCaptcha(ImageCaptcha imageCaptcha, String code);

    void findPassWord();
}
