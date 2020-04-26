package com.zb.lib_base.vm;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.iv.BaseVMInterface;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.windows.BottleQuestionPW;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.ViewPager;

public class BaseViewModel implements BaseVMInterface {
    public ViewDataBinding mBinding;
    public AppCompatActivity activity;
    public boolean isMore = false;

    @Override
    public void setBinding(ViewDataBinding binding) {
        mBinding = binding;
        activity = (AppCompatActivity) mBinding.getRoot().getContext();
    }

    @Override
    public void clickItem(DiscoverInfo discoverInfo) {
        ActivityUtils.getHomeDiscoverDetail(discoverInfo.getFriendDynId());
    }

    @Override
    public void back(View view) {

    }

    @Override
    public void right(View view) {

    }

    @Override
    public void more(View view) {

    }

    @Override
    public void follow(View view) {

    }

    @Override
    public void superLike(View view) {

    }

    @Override
    public void clean(View view) {

    }

    @Override
    public void question(View view) {
        new BottleQuestionPW(activity, mBinding.getRoot());
    }

    public void initTabLayout(String[] tabNames, TabLayout tabLayout, ViewPager viewPager, int selectColor, int color) {
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(tabNames[i]));
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                changeTab(tab, 18, selectColor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTab(tab, 14, color);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        changeTab(Objects.requireNonNull(tabLayout.getTabAt(0)), 18, selectColor);
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
     * 自定义Tab的View * @param currentPosition * @return
     */
    private View getTabView(String name) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_tab, null);
        TextView textView = view.findViewById(R.id.tab_item_textview);
        textView.setText(name);
        return view;
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param runnable      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, BaseActivity.PermissionCallback runnable, @NonNull String... permissions) {
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).performCodeWithPermission(permissionDes, runnable, permissions);
        }
    }
}
