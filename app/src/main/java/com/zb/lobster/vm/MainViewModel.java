package com.zb.lobster.vm;

import android.util.Log;

import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lobster.databinding.AcMainBinding;
import com.zb.lobster.iv.MainVMInterface;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;
    private int nowIndex = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mainBinding = (AcMainBinding) binding;
        initFragments();
        mainBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager()));
        mainBinding.viewPage.setOffscreenPageLimit(2);
        mainBinding.viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                nowIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mainBinding.setIndex(nowIndex);
            }
        });

        selectPage(0);
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFragment());
        fragments.add(FragmentUtils.getCardFragment());
        fragments.add(FragmentUtils.getChatFragment());
        fragments.add(FragmentUtils.getMineFragment());
    }

    @Override
    public void selectPage(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        mainBinding.setIndex(nowIndex);
        mainBinding.viewPage.setCurrentItem(index);
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
