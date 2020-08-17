package com.zb.module_card.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.zb.module_card.R;
import com.zb.module_card.databinding.ProgressBinding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class ProgressView extends RelativeLayout {
    private ProgressBinding mBinding;
    private static ObjectAnimator scaleX, scaleY;
    private static AnimatorSet animatorSet = new AnimatorSet();

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.progress, null, false);
        addView(mBinding.getRoot());
        scaleX = ObjectAnimator.ofFloat(mBinding.ivProgress, "scaleX", 1, 2.3f).setDuration(1000);
        scaleY = ObjectAnimator.ofFloat(mBinding.ivProgress, "scaleY", 1, 2.3f).setDuration(1000);

        scaleX.setRepeatCount(Animation.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.RESTART);

        scaleY.setRepeatCount(Animation.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.RESTART);
    }

    public static void play() {
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
    }

    public static void stop() {
        animatorSet.cancel();
    }

    @BindingAdapter("isPlay")
    public static void onClick(final ProgressView view, boolean isPlay) {
        if (isPlay) {
            play();
        } else {
            stop();
        }
    }
}
