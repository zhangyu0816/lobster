package com.zb.lobster.activity;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.StatusBarUtil;

public abstract class AppBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
//        StatusBarUtil.setLayoutPadding(this, ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
    }
}
