package com.zb.module_register;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.RouteUtils;

@Route(path = RouteUtils.Register_Login)
public class LoginActivity extends BaseActivity {

    @Override
    public int getRes() {
        return R.layout.register_login;
    }

    @Override
    public void initUI() {

    }
}
