package com.zb.module_home.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zb.lib_base.activity.BaseFragment;
import com.zb.lib_base.adapter.ViewPagerAdapter;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.vm.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

@Route(path = RouteUtils.Home_Fragment)
public class HomeFragment extends BaseFragment {
    private HomeFragBinding homeFragBinding;
    private List<Fragment> fragments = new ArrayList<>();
    private HomeViewModel viewModel;
    private ViewPagerAdapter adapter;
    private boolean createFragment = false;

    @Override
    public int getRes() {
        return R.layout.home_frag;
    }

    @Override
    public void initUI() {
        viewModel = new HomeViewModel();
        homeFragBinding = (HomeFragBinding) mBinding;
        mBinding.setVariable(BR.viewModel, viewModel);
    }

    private void initFragments() {
        viewModel.setBinding(mBinding);
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFollowFragment());
        fragments.add(FragmentUtils.getCardMemberDiscoverFragment(0));
        fragments.add(FragmentUtils.getCardMemberVideoFragment(0));
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
        homeFragBinding.viewPage.setSaveEnabled(false);
        homeFragBinding.viewPage.setAdapter(adapter);
        homeFragBinding.viewPage.setCurrentItem(1);
        viewModel.initTabLayout(new String[]{"关注", "推荐", "小视频"}, homeFragBinding.tabLayout, homeFragBinding.viewPage, R.color.black_252, R.color.black_827, 1,false);
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
            adapter.notifyItemChanged(homeFragBinding.viewPage.getCurrentItem());
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
