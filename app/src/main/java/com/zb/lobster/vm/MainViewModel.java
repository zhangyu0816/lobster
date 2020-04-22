package com.zb.lobster.vm;

import com.zb.lib_base.BR;
import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lobster.R;
import com.zb.lobster.databinding.AcMainBinding;
import com.zb.lobster.iv.MainVMInterface;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;
    private int nowIndex = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mainBinding = (AcMainBinding) binding;

        mBinding.setVariable(BR.guidanceRes, R.mipmap.guidance_left);
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(FragmentUtils.getHomeFragment());
        fragments.add(FragmentUtils.getCardFragment());
        fragments.add(FragmentUtils.getChatFragment());
        fragments.add(FragmentUtils.getMineFragment());
        mainBinding.viewPage.setAdapter(new FragmentAdapter(activity.getSupportFragmentManager(), fragments));
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
                if (nowIndex == 1) {
                    if (PreferenceUtil.readIntValue(activity, "showGuidance") == 0) {
                        mBinding.setVariable(BR.showGuidance, true);
                    }
                }

            }
        });

        selectPage(0);
    }

    @Override
    public void selectPage(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        mainBinding.setIndex(nowIndex);
        mainBinding.viewPage.setCurrentItem(index);
    }

    @Override
    public void chargeGuidance(int guidanceRes) {
        if (guidanceRes == R.mipmap.guidance_left) {
            mBinding.setVariable(BR.guidanceRes, R.mipmap.guidance_right);
        } else if (guidanceRes == R.mipmap.guidance_right) {
            mBinding.setVariable(BR.guidanceRes, R.mipmap.guidance_info);
        } else if (guidanceRes == R.mipmap.guidance_info) {
            mBinding.setVariable(BR.guidanceRes, R.mipmap.guidance_return);
        } else if (guidanceRes == R.mipmap.guidance_return) {
            mBinding.setVariable(BR.guidanceRes, R.mipmap.guidance_super);
        } else if (guidanceRes == R.mipmap.guidance_super) {
            mBinding.setVariable(BR.guidanceRes, R.mipmap.guidance_exposure);
        } else if (guidanceRes == R.mipmap.guidance_exposure) {
            mBinding.setVariable(BR.showGuidance, false);
            PreferenceUtil.saveIntValue(activity, "showGuidance", 1);
        }
    }

}
