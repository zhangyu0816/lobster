package com.zb.lib_base.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.databinding.SuperLikeBinding;
import com.zb.lib_base.iv.SuperLikeInterface;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.utils.ObjectUtils;

import java.util.Random;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class SuperLikeView extends RelativeLayout {
    private static long time = 500;
    private static Random ra = new Random();
    private static SuperLikeBinding mBinding;
    private static SuperLikeInterface mSuperLikeInterface;
    private static PairInfo mPairInfo;
    private static View mCurrentView;
    private static Handler handler = new Handler();
    private static Runnable runnable = SuperLikeView::play;

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

    private void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.super_like, null, false);
        addView(mBinding.getRoot());
        AdapterBinding.onClick(mBinding.likeLayout, view -> {
            if (mSuperLikeInterface != null)
                mSuperLikeInterface.superLike(mCurrentView, mPairInfo);
        });
        AdapterBinding.onClick(mBinding.returnLayout, view -> {
            if (mSuperLikeInterface != null)
                mSuperLikeInterface.returnBack();
        });
    }

    private static PropertyValuesHolder pvhTY, pvhTX;
    private static ObjectAnimator pvh_star1, pvh_star2;

    private static void createAnimator() {
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
        pvhTX = PropertyValuesHolder.ofFloat("translationX", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
        pvh_star1 = ObjectAnimator.ofPropertyValuesHolder(mBinding.ivStar1, pvhTY, pvhTX).setDuration(time);

        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
        pvhTX = PropertyValuesHolder.ofFloat("translationX", 0, (ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
        pvh_star2 = ObjectAnimator.ofPropertyValuesHolder(mBinding.ivStar2, pvhTY, pvhTX).setDuration(time);
    }


    private static void play() {
        if (pvh_star1 != null && !pvh_star1.isRunning() && pvh_star2 != null && !pvh_star2.isRunning()) {
            mBinding.ivStar1.setVisibility(VISIBLE);
            mBinding.ivStar2.setVisibility(VISIBLE);

            pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
            pvhTX = PropertyValuesHolder.ofFloat("translationX", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
            pvh_star1.setValues(pvhTY, pvhTX);
            pvh_star1.start();

            pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
            pvhTX = PropertyValuesHolder.ofFloat("translationX", 0, (ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50));
            pvh_star2.setValues(pvhTY, pvhTX);
            pvh_star2.start();
            if (handler == null)
                handler = new Handler();
            handler.postDelayed(runnable, time + 2000);
            handler.postDelayed(() -> {
                mBinding.ivStar1.setVisibility(GONE);
                mBinding.ivStar2.setVisibility(GONE);
                pvh_star1.cancel();
                pvh_star2.cancel();
            }, time);
        }
    }

    public static void stop() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        handler = null;
        if (pvh_star1 != null && pvh_star1.isRunning()) {
            pvh_star1.cancel();
        }
        if (pvh_star2 != null && pvh_star2.isRunning())
            pvh_star2.cancel();
    }

    @BindingAdapter(value = {"superLikeInterface", "pairInfo", "currentView", "isPlay", "isAnimator"}, requireAll = false)
    public static void superLike(SuperLikeView view, SuperLikeInterface superLikeInterface, PairInfo pairInfo, View currentView, boolean isPlay, boolean isAnimator) {
        mSuperLikeInterface = superLikeInterface;
        mPairInfo = pairInfo;
        mCurrentView = currentView;
        if (isAnimator) {
            createAnimator();
            if (isPlay)
                play();
            else
                stop();
        }
    }
}
