package com.zb.module_mine;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.StatusBarUtil;

public abstract class MineBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MineTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
//        StatusBarUtil.setLayoutPadding(this, ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
    }
}
