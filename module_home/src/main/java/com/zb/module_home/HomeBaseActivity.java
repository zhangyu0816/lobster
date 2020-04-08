package com.zb.module_home;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;

public abstract class HomeBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.HomeTheme);
        super.onCreate(savedInstanceState);
    }
}
