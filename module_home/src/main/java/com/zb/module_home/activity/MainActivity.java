package com.zb.module_home.activity;

import com.zb.module_home.R;
import com.zb.module_home.fragment.HomeFragment;

public class MainActivity extends HomeBaseActivity {

    @Override
    public int getRes() {
        return R.layout.home_main;
    }

    @Override
    public void initUI() {
        getSupportFragmentManager().beginTransaction().replace(R.id.tv_home, new HomeFragment()).commit();
    }
}
