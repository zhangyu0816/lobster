package com.zb.module_card.activity;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_card.R;

public abstract class CardBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CardTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightModeNotFull(this);
    }
}
