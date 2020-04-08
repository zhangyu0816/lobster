package com.zb.module_mine;

import com.zb.lib_base.utils.FragmentUtils;

public class MainActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_main;
    }

    @Override
    public void initUI() {
        findViewById(R.id.tv_mine).setOnClickListener(v -> {
            FragmentUtils.getMineFragment();
        });
    }
}
