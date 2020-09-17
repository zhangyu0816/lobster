package com.zb.module_chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.StatusBarUtil;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.vm.ChatViewModel;

@Route(path = RouteUtils.Chat_Activity)
public class ChatActivity extends BaseActivity {
    @Autowired(name = "otherUserId")
    long otherUserId;
    @Autowired(name = "isNotice")
    boolean isNotice;

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
        viewModel.isNotice = isNotice;
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.isVoice, false);
        mBinding.setVariable(BR.isEmoji, false);
        mBinding.setVariable(BR.content, "");
        cameraReceiver = new BaseReceiver(activity, "lobster_camera") {
            @Override
            public void onReceive(Context context, Intent intent) {
                MineApp.isChat = false;
                int cameraType = intent.getIntExtra("cameraType", 0);
                if (cameraType == 0 || cameraType == 2)
                    viewModel.uploadImage(intent.getStringExtra("filePath"));
                else if (cameraType == 1) {
                    viewModel.uploadVideo(intent.getStringExtra("filePath"), intent.getLongExtra("time", 0));
                }
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
        try {
            cameraReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
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
