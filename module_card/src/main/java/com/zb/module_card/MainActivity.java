package com.zb.module_card;

public class MainActivity extends CardBaseActivity {
    @Override
    public int getRes() {
        return R.layout.card_main;
    }

    @Override
    public void initUI() {

        getSupportFragmentManager().beginTransaction().replace(R.id.tv_card, new CardFragment()).commit();
    }
}
