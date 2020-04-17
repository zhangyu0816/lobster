package com.zb.module_bottle.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_bottle.R;

@Route(path = RouteUtils.Bottle_Main)
public class MainActivity extends BottleBaseActivity {

    @Override
    public int getRes() {
        return R.layout.bottle_main;
    }

    @Override
    public void initUI() {

    }
}
