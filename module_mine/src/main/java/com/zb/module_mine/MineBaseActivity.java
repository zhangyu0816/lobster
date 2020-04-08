package com.zb.module_mine;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;

public abstract class MineBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MineTheme);
        super.onCreate(savedInstanceState);
    }
}
