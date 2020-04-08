package com.zb.module_chat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.vm.ChatViewModel;

@Route(path = RouteUtils.Chat_Fragment)
public class ChatFragment extends BaseFragment {


    @Override
    public int getRes() {
        return R.layout.chat_frag;
    }

    @Override
    public void initUI() {
        ChatViewModel viewModel = new ChatViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel,viewModel);
    }
}
