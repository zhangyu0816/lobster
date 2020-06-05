package com.zb.module_chat.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.vm.ChatPairViewModel;

@Route(path = RouteUtils.Chat_Pair_Fragment)
public class ChatPairFragment extends BaseFragment {
    private ChatPairViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.chat_pair_fragment;
    }

    @Override
    public void initUI() {
        viewModel = new ChatPairViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
