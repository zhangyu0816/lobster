package com.zb.module_register;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;

public abstract class RegisterBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.RegisterTheme);
        super.onCreate(savedInstanceState);
    }
}
