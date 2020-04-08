package com.zb.module_chat;

import android.os.Bundle;

import com.zb.lib_base.activity.BaseActivity;

public abstract class ChatBaseActivity  extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ChatTheme);
        super.onCreate(savedInstanceState);
    }
}
