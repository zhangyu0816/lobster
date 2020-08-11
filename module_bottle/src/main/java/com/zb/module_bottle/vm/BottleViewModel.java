package com.zb.module_bottle.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.databinding.BottleMainBinding;
import com.zb.module_bottle.iv.BottleVMInterface;

import androidx.databinding.ViewDataBinding;

public class BottleViewModel extends BaseViewModel implements BottleVMInterface {
    private BottleMainBinding mBinding;
    private AnimatorSet animatorSet = new AnimatorSet();
    private long time = 800;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleMainBinding) binding;
    }

    @Override
    public void entryBottle(View view) {
        if (mBinding.getIsOutLine()) {
            SCToastUtil.showToast(activity, "当前网络异常，请检查是否连接", true);
            return;
        }
        mBinding.tvBtn.setVisibility(View.GONE);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(mBinding.ivStar, "translationX", 0, MineApp.W - ObjectUtils.getViewSizeByWidthFromMax(559)).setDuration(time);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBinding.ivBg, "scaleX", 1, 2).setDuration(time);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBinding.ivBg, "scaleY", 1, 2).setDuration(time);

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY, translateX);//同时执行
        animatorSet.start();

        new Handler().postDelayed(() -> {
            ActivityUtils.getBottleThrow();
            activity.finish();
        }, time);

    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }
}
