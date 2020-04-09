package com.zb.module_home.vm;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.databinding.HomeFragBinding;
import com.zb.module_home.iv.HomeVMInterface;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class HomeViewModel extends BaseViewModel implements HomeVMInterface {
    private HomeFragBinding homeFragBinding;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] tabNames = new String[]{"关注", "推荐", "小程序"};

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        homeFragBinding = (HomeFragBinding) binding;
        initFragments();
        homeFragBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager()));

        initTabLayout();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFollowFragment());
        fragments.add(FragmentUtils.getHomeRecommendFragment());
        fragments.add(FragmentUtils.getHomeVideoFragment());
    }

    private void initTabLayout() {
        homeFragBinding.tabLayout.setupWithViewPager(homeFragBinding.viewPage);

        for (int i = 0; i < homeFragBinding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = homeFragBinding.tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        homeFragBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                homeFragBinding.viewPage.setCurrentItem(tab.getPosition());
                changeTab(tab, 18, R.color.black_252);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTab(tab, 14, R.color.black_827);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        changeTab(Objects.requireNonNull(homeFragBinding.tabLayout.getTabAt(0)), 18, R.color.black_252);
    }

    // 改变选中状态
    private void changeTab(TabLayout.Tab tab, int size, int color) {
        View view = tab.getCustomView();
        if (view instanceof TextView) {
            // 改变 tab 选择状态下的字体大小
            ((TextView) view).setTextSize(size);
            ((TextView) view).setTextColor(ContextCompat.getColor(activity, color));
        }
    }

    /**
     * 适配器
     */
    public class FragmentAdapter extends FragmentStatePagerAdapter {
        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * 自定义Tab的View * @param currentPosition * @return
     */
    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
        textView.setText(tabNames[currentPosition]);
        return view;
    }
}
