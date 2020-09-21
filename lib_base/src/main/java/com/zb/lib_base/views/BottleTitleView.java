package com.zb.lib_base.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.zb.lib_base.R;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class BottleTitleView extends RelativeLayout {

    private static AnimatorSet animatorSet = new AnimatorSet();

    public BottleTitleView(Context context) {
        super(context);
        init(context);
    }

    public BottleTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BottleTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        com.zb.lib_base.databinding.BottleTitleBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottle_title, null, false);
        addView(mBinding.getRoot());

        int y = 30;
        ObjectAnimator ivBackY = ObjectAnimator.ofFloat(mBinding.ivBackWeak, "translationY", y, 0, y, 0, y, 0, y);
        ObjectAnimator ivFrontY = ObjectAnimator.ofFloat(mBinding.ivFrontWeak, "translationY", 0, y, 0, y, 0, y, 0);
        ObjectAnimator ivBottleY = ObjectAnimator.ofFloat(mBinding.ivBottle, "translationY", y, 0, y, 0, y, 0, y);
        ObjectAnimator ivLightY = ObjectAnimator.ofFloat(mBinding.ivBottleLight, "translationY", y, 0, y, 0, y, 0, y);

        long time = 10000;
        ivBackY.setDuration(time);
        int repeat = Animation.INFINITE;
        ivBackY.setRepeatCount(repeat);
        ivBackY.setRepeatMode(ValueAnimator.RESTART);

        ivFrontY.setDuration(time);
        ivFrontY.setRepeatCount(repeat);
        ivFrontY.setRepeatMode(ValueAnimator.RESTART);

        ivBottleY.setDuration(time);
        ivBottleY.setRepeatCount(repeat);
        ivBottleY.setRepeatMode(ValueAnimator.RESTART);

        ivLightY.setDuration(time);
        ivLightY.setRepeatCount(repeat);
        ivLightY.setRepeatMode(ValueAnimator.RESTART);

        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(ivBackY, ivFrontY, ivBottleY, ivLightY);//同时执行

    }

    private static void start() {
        if (animatorSet != null && !animatorSet.isRunning())
            animatorSet.start();
    }

    private static void stop() {
        if (animatorSet != null && animatorSet.isRunning())
            animatorSet.cancel();
    }

    @BindingAdapter("isPlay")
    public static void bottleTitle(BottleTitleView view, boolean isPlay) {
        if (isPlay)
            start();
        else
            stop();
    }
}
