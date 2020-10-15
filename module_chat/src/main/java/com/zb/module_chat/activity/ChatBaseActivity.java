package com.zb.module_chat.activity;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_chat.R;

public abstract class ChatBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ChatTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
    }
}
