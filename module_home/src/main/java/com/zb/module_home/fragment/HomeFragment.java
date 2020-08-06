package com.zb.module_home.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.vm.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

@Route(path = RouteUtils.Home_Fragment)
public class HomeFragment extends BaseFragment {
    private HomeFragBinding homeFragBinding;
    private List<Fragment> fragments = new ArrayList<>();
    private HomeViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.home_frag;
    }

    @Override
    public void initUI() {
        viewModel = new HomeViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        homeFragBinding = (HomeFragBinding) mBinding;
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFollowFragment());
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(0));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(0));
        homeFragBinding.viewPage.setAdapter(new FragmentAdapter(getChildFragmentManager(), fragments));
        viewModel.initTabLayout(new String[]{"关注", "推荐", "小视频"}, homeFragBinding.tabLayout, homeFragBinding.viewPage, R.color.black_252, R.color.black_827,1);
    }
}
