package com.zb.module_home;

import com.zb.lib_base.utils.FragmentUtils;

public class MainActivity extends HomeBaseActivity {

    @Override
    public int getRes() {
        return R.layout.home_main;
    }

    @Override
    public void initUI() {
        findViewById(R.id.tv_home).setOnClickListener(v -> {
            FragmentUtils.getHomeFragment();
        });
    }
}
