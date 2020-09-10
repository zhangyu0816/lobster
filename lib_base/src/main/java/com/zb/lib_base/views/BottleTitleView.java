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
import com.zb.lib_base.databinding.BottleTitleBinding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class BottleTitleView extends RelativeLayout {

    private BottleTitleBinding mBinding;
    private static AnimatorSet animatorSet = new AnimatorSet();
    private long time = 10000;
    private int repeat = Animation.INFINITE;
    private int Y = 30;

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

    private ObjectAnimator ivBackY, ivFrontY, ivBottleY, ivLightY;

    private void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottle_title, null, false);
        addView(mBinding.getRoot());

        ivBackY = ObjectAnimator.ofFloat(mBinding.ivBackWeak, "translationY", Y, 0, Y, 0, Y, 0, Y);
        ivFrontY = ObjectAnimator.ofFloat(mBinding.ivFrontWeak, "translationY", 0, Y, 0, Y, 0, Y, 0);
        ivBottleY = ObjectAnimator.ofFloat(mBinding.ivBottle, "translationY", Y, 0, Y, 0, Y, 0, Y);
        ivLightY = ObjectAnimator.ofFloat(mBinding.ivBottleLight, "translationY", Y, 0, Y, 0, Y, 0, Y);

        ivBackY.setDuration(time);
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
