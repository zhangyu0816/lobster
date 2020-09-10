package com.zb.module_chat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.app.MineApp;
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
    private BaseReceiver updateRedReceiver;

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

        updateRedReceiver = new BaseReceiver(activity, "lobster_updateRed") {
            @Override
            public void onReceive(Context context, Intent intent) {
                String temp = "聊天-" + (viewModel.chatListDb.getChatTabRed() > 0 ? "true" : "false");
                viewModel.initTabLayout(new String[]{"所有匹配", temp}, binding.tabLayout, binding.viewPage, R.color.black_252, R.color.black_827, MineApp.chatSelectIndex);
            }
        };
        new Handler().postDelayed(this::initFragments, 800);
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getChatPairFragment());
        fragments.add(FragmentUtils.getChatListFragment());
        binding.viewPage.setAdapter(new FragmentAdapter(getChildFragmentManager(), fragments));
        String temp = "聊天-" + (viewModel.chatListDb.getChatTabRed() > 0 ? "true" : "false");
        viewModel.initTabLayout(new String[]{"所有匹配", temp}, binding.tabLayout, binding.viewPage, R.color.black_252, R.color.black_827, MineApp.chatSelectIndex);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateRedReceiver.unregisterReceiver();
        viewModel.onDestroy();
    }
}
