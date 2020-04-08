package com.zb.lobster.vm;

import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lobster.databinding.AcMainBinding;
import com.zb.lobster.iv.MainVMInterface;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mainBinding = (AcMainBinding) binding;
        initFragments();
        mainBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager()));
        mainBinding.viewPage.setOffscreenPageLimit(2);
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
        mainBinding.setIndex(index);
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
