package com.yimi.rentme.iv;

import android.view.View;

public interface LoginVMInterface {

    void cleanPhone(View view);

    void selectSex(int sex);

    void selectBirthday(View view);

    void resetCode(View view);

    void upload(View view);

    void toQQ(View view);

    void toWX(View view);

    void next(View view);

    void changeUrl(View view);

    void selectTag(View view);

    void close(View view);

    void selectJog(View view);

    void editSign(View view);

    void clickSelect(View view);

    void registerCaptcha();

    void loginCaptcha();

    void register();

    void loginByUnion();

    void loginByCaptcha();

    void loginByPass();

    void checkUserName();
}
