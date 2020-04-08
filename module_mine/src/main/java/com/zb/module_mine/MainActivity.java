package com.zb.module_mine;

public class MainActivity extends MineBaseActivity {
    @Override
    public int getRes() {
        return R.layout.mine_main;
    }

    @Override
    public void initUI() {

        getSupportFragmentManager().beginTransaction().replace(R.id.tv_mine, new MineFragment()).commit();
    }
}
