package com.zb.module_chat;

import com.zb.lib_base.utils.FragmentUtils;

public class MainActivity extends ChatBaseActivity {
    @Override
    public int getRes() {
        return R.layout.chat_main;
    }

    @Override
    public void initUI() {
        findViewById(R.id.tv_chat).setOnClickListener(v -> {
            FragmentUtils.getChatFragment();
        });
    }
}
