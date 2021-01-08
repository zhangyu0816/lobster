package com.zb.module_chat.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.vm.ChatListViewModel;

@Route(path = RouteUtils.Chat_List_Fragment)
public class ChatListFragment extends BaseFragment {
    private ChatListViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.chat_list_fragment;
    }

    @Override
    public void initUI() {
        viewModel = new ChatListViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }
}
