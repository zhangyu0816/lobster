package com.zb.module_chat.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.databinding.ChatFragBinding;
import com.zb.module_chat.vm.ChatFragViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

@Route(path = RouteUtils.Chat_Fragment)
public class ChatFragment extends BaseFragment {

    private ChatFragBinding binding;
    private List<Fragment> fragments = new ArrayList<>();
    private ChatFragViewModel viewModel;
    private ViewPagerAdapter adapter;
    private boolean createFragment = false;

    @Override
    public int getRes() {
        return R.layout.chat_frag;
    }

    @Override
    public void initUI() {
        viewModel = new ChatFragViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        binding = (ChatFragBinding) mBinding;
    }

    private void initFragments() {
        viewModel.setBinding(mBinding);
        fragments.clear();
        fragments.add(FragmentUtils.getChatPairFragment());
        fragments.add(FragmentUtils.getChatListFragment());
        adapter = new ViewPagerAdapter(getChildFragmentManager(), new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        }, fragments);
        binding.viewPage.setSaveEnabled(false);
        binding.viewPage.setAdapter(adapter);
        String temp = "聊天-" + (ChatListDb.getInstance().getChatTabRed() > 0 ? "true" : "false");
        MineApp.chatSelectIndex = 0;
        viewModel.initTabLayout(new String[]{"所有匹配", temp}, binding.tabLayout, binding.viewPage, R.color.black_252, R.color.black_827, MineApp.chatSelectIndex,true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null)
            viewModel.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.notifyItemChanged(binding.viewPage.getCurrentItem());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!createFragment) {
            createFragment = true;
            initFragments();
        }
    }
}
