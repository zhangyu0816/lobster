package com.zb.module_home.vm;

import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.iv.HomeVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {
    private HomeFragBinding homeFragBinding;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        homeFragBinding = (HomeFragBinding) binding;
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFollowFragment());
        fragments.add(FragmentUtils.getHomeRecommendFragment());
        fragments.add(FragmentUtils.getHomeVideoFragment());
        homeFragBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
        initTabLayout(new String[]{"关注", "推荐", "小视频"}, homeFragBinding.tabLayout, homeFragBinding.viewPage, R.color.black_252, R.color.black_827);
    }

}
