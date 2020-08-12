package com.zb.lib_base.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.zb.lib_base.R;
import com.zb.lib_base.databinding.SuperLikeBinding;
import com.zb.lib_base.utils.ObjectUtils;

import java.util.Random;

import androidx.databinding.DataBindingUtil;

public class SuperLikeView extends RelativeLayout {
    private long time = 2000;
    private int repeat = 50000;
    private AnimatorSet animatorSet = new AnimatorSet();
    private Random ra = new Random();
    private float scale = 2;
    private SuperLikeBinding mBinding;

    public SuperLikeView(Context context) {
        super(context);
        init(context);
    }

    public SuperLikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuperLikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ObjectAnimator ivStar1X, ivStar1Y, ivStar2X, ivStar2Y, ivStar1A, ivStar2A, scale1X, scale1Y, scale2X, scale2Y;

    private void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.super_like, null, false);
        addView(mBinding.getRoot());
        play();

    }

    private void play() {
        mBinding.ivStar1.setVisibility(VISIBLE);
        mBinding.ivStar2.setVisibility(VISIBLE);
        ivStar1X = ObjectAnimator.ofFloat(mBinding.ivStar1, "translationX", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(300)-150) + 50)).setDuration(time);
        ivStar1Y = ObjectAnimator.ofFloat(mBinding.ivStar1, "translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(300)-150) + 50)).setDuration(time);

        ivStar2X = ObjectAnimator.ofFloat(mBinding.ivStar2, "translationX", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(300)-150) + 50)).setDuration(time);
        ivStar2Y = ObjectAnimator.ofFloat(mBinding.ivStar2, "translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(300)-150)+ 50)).setDuration(time);

        ivStar1A = ObjectAnimator.ofFloat(mBinding.ivStar1, "alpha", 1, 0.3f).setDuration(time);
        ivStar2A = ObjectAnimator.ofFloat(mBinding.ivStar2, "alpha", 1, 0.3f).setDuration(time);

        scale1X = ObjectAnimator.ofFloat(mBinding.ivStar1, "scaleX", 1, scale).setDuration(time);
        scale1Y = ObjectAnimator.ofFloat(mBinding.ivStar1, "scaleY", 1, scale).setDuration(time);

        scale2X = ObjectAnimator.ofFloat(mBinding.ivStar2, "scaleX", 1, scale).setDuration(time);
        scale2Y = ObjectAnimator.ofFloat(mBinding.ivStar2, "scaleY", 1, scale).setDuration(time);


        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(ivStar1X)
                .with(ivStar1Y)
                .with(ivStar2X)
                .with(ivStar2Y)
                .with(ivStar1A)
                .with(ivStar2A)
                .with(scale1X)
                .with(scale1Y)
                .with(scale2X)
                .with(scale2Y);
        animatorSet.start();

        new Handler().postDelayed(() -> {
            mBinding.ivStar1.setVisibility(GONE);
            mBinding.ivStar2.setVisibility(GONE);
        }, time);

        new Handler().postDelayed(this::play, (time + 3000));
    }

}
