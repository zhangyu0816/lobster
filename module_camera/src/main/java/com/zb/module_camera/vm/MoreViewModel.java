package com.zb.module_camera.vm;

import android.view.View;

import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_camera.databinding.CameraMoreBinding;
import com.zb.module_camera.iv.MoreVMInterface;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MoreViewModel extends BaseViewModel implements MoreVMInterface {
    private CameraMoreBinding cameraMoreBinding;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int nowIndex = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        cameraMoreBinding = (CameraMoreBinding) binding;
        initFragments();
        cameraMoreBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager()));
        cameraMoreBinding.viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                nowIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                cameraMoreBinding.setIndex(nowIndex);
            }
        });

        selectIndex(0);
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getCameraPictureFragment());
        fragments.add(FragmentUtils.getCameraVideoFragment());
    }

    @Override
    public void selectIndex(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        cameraMoreBinding.setIndex(nowIndex);
        cameraMoreBinding.viewPage.setCurrentItem(index);
    }

    @Override
    public void selectCamera(View view) {

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
