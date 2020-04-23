package com.zb.module_mine.activity;

import com.zb.module_mine.R;
import com.zb.module_mine.fragment.MineFragment;

public class MainActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_main;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        getSupportFragmentManager().beginTransaction().replace(R.id.tv_mine, new MineFragment()).commit();
    }
}
