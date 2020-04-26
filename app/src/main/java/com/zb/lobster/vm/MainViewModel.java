package com.zb.lobster.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.zb.lib_base.adapter.FragmentAdapter;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.FragmentUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lobster.BR;
import com.zb.lobster.databinding.AcMainBinding;
import com.zb.lobster.iv.MainVMInterface;
import com.zb.module_card.windows.GuidancePW;

import java.util.ArrayList;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MainViewModel extends BaseViewModel implements MainVMInterface {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private AcMainBinding mainBinding;
    private int nowIndex = -1;
    AnimatorSet animatorSet = new AnimatorSet();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mainBinding = (AcMainBinding) binding;
        mainBinding.tvTitle.setTypeface(MineApp.simplifiedType);
        mainBinding.tvContent.setTypeface(MineApp.simplifiedType);
        mainBinding.tvSubContent.setTypeface(MineApp.simplifiedType);
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
                if (state == 0 && nowIndex == 1 && PreferenceUtil.readIntValue(activity, "showGuidance") == 0) {
                    new GuidancePW(activity, mBinding.getRoot());
                }
            }
        });

        selectPage(0);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainBinding.remindRelative.getLayoutParams();
        params.setMarginEnd(ObjectUtils.getViewSizeByWidthFromMax(220));
        mainBinding.remindRelative.setLayoutParams(params);

        mainBinding.tvTitle.setText("24小时内有");
        mainBinding.tvContent.setText("99+");
        mainBinding.tvSubContent.setText("人喜欢你啦");

        mBinding.setVariable(BR.otherHead, MineApp.logo);


//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleX", 0, 1).setDuration(500);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleY", 0, 1).setDuration(500);
//        ObjectAnimator translateY = ObjectAnimator.ofFloat(mainBinding.remindRelative, "translationY", 0, -30, 0, -30, 0, -30, 0).setDuration(500);
//        ObjectAnimator scaleXEnd = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleX", 1, 0).setDuration(500);
//        ObjectAnimator scaleYEnd = ObjectAnimator.ofFloat(mainBinding.remindRelative, "scaleY", 1, 0).setDuration(500);
//
//        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
//        animatorSet.play(scaleX).with(scaleY).after(5000);
//        animatorSet.play(translateY).after(scaleY);
//        animatorSet.play(scaleXEnd).with(scaleYEnd).after(translateY).after(10000);
//        animatorSet.start();
    }

    @Override
    public void selectPage(int index) {
        if (nowIndex == index)
            return;
        nowIndex = index;
        mainBinding.setIndex(nowIndex);
        mainBinding.viewPage.setCurrentItem(index);
    }

    public void stopAnimator() {
        animatorSet.cancel();
    }
}
