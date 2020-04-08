package com.zb.module_home.vm;

import com.google.android.material.tabs.TabLayout;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.iv.HomeVMInterface;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {
    private HomeFragBinding homeFragBinding;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int _position = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        homeFragBinding = (HomeFragBinding) binding;
        initFragments();
        homeFragBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager()));
        homeFragBinding.viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                _position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    homeFragBinding.tabLayout.selectTab(homeFragBinding.tabLayout.getTabAt(_position),true);
                }
            }
        });
        initTabLayout();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFollowFragment());
        fragments.add(FragmentUtils.getHomeRecommendFragment());
        fragments.add(FragmentUtils.getHomeVideoFragment());
    }

    private void initTabLayout() {
        homeFragBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                homeFragBinding.viewPage.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 适配器
     */
    public class FragmentAdapter extends FragmentStatePagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }
}
