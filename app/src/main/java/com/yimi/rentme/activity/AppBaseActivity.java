package com.yimi.rentme.activity;

import android.os.Bundle;

import com.yimi.rentme.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.StatusBarUtil;

public abstract class AppBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        StatusBarUtil.statusBarLightMode(this);
    }
}
