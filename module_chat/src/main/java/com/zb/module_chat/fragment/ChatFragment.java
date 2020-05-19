package com.zb.module_chat.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.databinding.ChatFragBinding;
import com.zb.module_chat.vm.ChatFragViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

@Route(path = RouteUtils.Chat_Fragment)
public class ChatFragment extends BaseFragment {

    private ChatFragBinding binding;
    private List<Fragment> fragments = new ArrayList<>();
    private ChatFragViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.chat_frag;
    }

    @Override
    public void initUI() {
        viewModel = new ChatFragViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        binding = (ChatFragBinding) mBinding;
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getChatPairFragment());
        fragments.add(FragmentUtils.getChatListFragment());
        binding.viewPage.setAdapter(new FragmentAdapter(getChildFragmentManager(), fragments));
        viewModel.initTabLayout(new String[]{"所有匹配", "聊天"}, binding.tabLayout, binding.viewPage, R.color.black_252, R.color.black_827);
    }
}
