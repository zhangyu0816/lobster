package com.zb.lib_base.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.zb.lib_base.R;
import com.zb.lib_base.databinding.SuperLikeBigBinding;
import com.zb.lib_base.utils.ObjectUtils;

import java.util.Random;

import androidx.databinding.DataBindingUtil;

public class SuperLikeBigView extends RelativeLayout {
    private long time = 500;
    private AnimatorSet animatorSet = new AnimatorSet();
    private Random ra = new Random();
    private SuperLikeBigBinding mBinding;

    public SuperLikeBigView(Context context) {
        super(context);
        init(context);
    }

    public SuperLikeBigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuperLikeBigView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ObjectAnimator ivStar1X, ivStar1Y, ivStar2X, ivStar2Y;

    private void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.super_like_big, null, false);
        addView(mBinding.getRoot());
        play();

    }

    private void play() {
        mBinding.ivStar1.setVisibility(VISIBLE);
        mBinding.ivStar2.setVisibility(VISIBLE);
        ivStar1X = ObjectAnimator.ofFloat(mBinding.ivStar1, "translationX", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50)).setDuration(time);
        ivStar1Y = ObjectAnimator.ofFloat(mBinding.ivStar1, "translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50)).setDuration(time);

        ivStar2X = ObjectAnimator.ofFloat(mBinding.ivStar2, "translationX", 0, (ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50)).setDuration(time);
        ivStar2Y = ObjectAnimator.ofFloat(mBinding.ivStar2, "translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200))+ 50)).setDuration(time);


        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(ivStar1X)
                .with(ivStar1Y)
                .with(ivStar2X)
                .with(ivStar2Y);
        animatorSet.start();

        new Handler().postDelayed(() -> {
            mBinding.ivStar1.setVisibility(GONE);
            mBinding.ivStar2.setVisibility(GONE);
        }, time);

        new Handler().postDelayed(this::play, (time + 2000));
    }

}
