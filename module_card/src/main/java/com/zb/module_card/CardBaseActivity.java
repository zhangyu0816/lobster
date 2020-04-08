package com.zb.module_card;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;

public abstract class CardBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CardTheme);
        super.onCreate(savedInstanceState);
    }
}
