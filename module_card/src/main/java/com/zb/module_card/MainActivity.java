package com.zb.module_card;

import com.zb.lib_base.utils.FragmentUtils;

public class MainActivity extends CardBaseActivity {
    @Override
    public int getRes() {
        return R.layout.card_main;
    }

    @Override
    public void initUI() {
        findViewById(R.id.tv_card).setOnClickListener(v -> {
            FragmentUtils.getCardFragment();
        });
    }
}
