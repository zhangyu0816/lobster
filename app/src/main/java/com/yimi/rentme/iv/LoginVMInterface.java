package com.yimi.rentme.iv;

import android.view.View;

public interface LoginVMInterface {

    void selectSex(int sex);

    void selectBirthday(View view);

    void resetCode(View view);

    void upload(View view);

    void selectImage(int position);

    void toLogin(View view);

    void toQQ(View view);

    void toWX(View view);

    void next(View view);

    void changeUrl(View view);

    void registerCaptcha();

    void loginCaptcha();

    void register();

    void loginByUnion();

    void loginByCaptcha();

    void loginByPass();
}
