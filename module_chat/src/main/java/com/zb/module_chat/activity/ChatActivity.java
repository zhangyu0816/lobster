package com.zb.module_chat.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.vm.ChatViewModel;

@Route(path = RouteUtils.Chat_Activity)
public class ChatActivity extends ChatBaseActivity {
    @Override
    public int getRes() {
        return R.layout.chat_chat;
    }

    @Override
    public void initUI() {
        ChatViewModel viewModel = new ChatViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
