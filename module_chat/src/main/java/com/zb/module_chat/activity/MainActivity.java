package com.zb.module_chat.activity;

import com.zb.module_chat.R;
import com.zb.module_chat.fragment.ChatFragment;

public class MainActivity extends ChatBaseActivity {
    @Override
    public int getRes() {
        return R.layout.chat_main;
    }

    @Override
    public void initUI() {
        getSupportFragmentManager().beginTransaction().replace(R.id.tv_chat, new ChatFragment()).commit();
    }
}