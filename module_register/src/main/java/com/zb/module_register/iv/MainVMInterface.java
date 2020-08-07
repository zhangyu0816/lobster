package com.zb.module_register.iv;

import android.view.View;

public interface MainVMInterface {

    void selectSex(int sex);

    void toLogin(View view);

    void changeUrl(View view);

    void registerByUnion(String userName, String captcha);

    void loginByUnion();

    void toQQ(View view);

    void toWX(View view);
}
