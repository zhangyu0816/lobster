package com.zb.module_chat.activity;

import android.content.Intent;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.vm.BaseChatViewModel;
import com.zb.module_chat.R;

@Route(path = RouteUtils.Chat_Activity)
public class ChatActivity extends ChatBaseActivity {
    @Autowired(name = "otherUserId")
    long otherUserId;
    @Autowired(name = "isNotice")
    boolean isNotice;

    private BaseChatViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.base_chat;
    }

    @Override
    public void initUI() {
        fitComprehensiveScreen();
        viewModel = new BaseChatViewModel();
        viewModel.otherUserId = otherUserId;
        viewModel.isNotice = isNotice;
        viewModel.msgChannelType = 1;
        viewModel.setBinding(mBinding);
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
        if (viewModel != null){
            viewModel.onDestroy();
            mBinding = null;
            viewModel = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewModel != null)
                viewModel.back(null);
            return true;
        }
        return false;
    }
}
