package com.zb.module_chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.vm.ChatViewModel;

@Route(path = RouteUtils.Chat_Activity)
public class ChatActivity extends BaseActivity {
    @Autowired(name = "otherUserId")
    long otherUserId;

    private ChatViewModel viewModel;
    private BaseReceiver cameraReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ChatTheme);
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightModeNotFull(this);
    }

    @Override
    public int getRes() {
        return R.layout.chat_chat;
    }

    @Override
    public void initUI() {
        viewModel = new ChatViewModel();
        viewModel.otherUserId = otherUserId;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.isVoice, false);
        mBinding.setVariable(BR.isEmoji, false);
        mBinding.setVariable(BR.content, "");
        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                viewModel.uploadImage(intent.getStringExtra("filePath"));
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            viewModel.uploadImage(data.getStringExtra("fileName"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraReceiver.unregisterReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(null);
            return true;
        }
        return false;
    }
}
