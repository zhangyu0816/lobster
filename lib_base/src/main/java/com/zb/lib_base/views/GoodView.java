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
import com.zb.lib_base.databinding.GoodLayoutBinding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class GoodView extends RelativeLayout {
    private GoodLayoutBinding mBinding;
    private ObjectAnimator ivCircleScaleX, ivCircleScaleY;
    private ObjectAnimator ivUnLikeScaleX, ivUnLikeScaleY;
    private ObjectAnimator ivLikeScaleX, ivLikeScaleY;
    private AnimatorSet animatorSet = new AnimatorSet();

    public GoodView(Context context) {
        super(context);
        init(context);
    }

    public GoodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoodView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.good_layout, null, false);
        addView(mBinding.getRoot());

        animatorSet.setInterpolator(new LinearInterpolator());
    }

    public void playUnlike() {
        mBinding.ivUnLike.setVisibility(VISIBLE);
        mBinding.ivCircle.setVisibility(GONE);
        mBinding.ivLike.setVisibility(GONE);

        ivUnLikeScaleX = ObjectAnimator.ofFloat(mBinding.ivUnLike, "scaleX", 0, 1, 0.8f, 1).setDuration(500);
        ivUnLikeScaleY = ObjectAnimator.ofFloat(mBinding.ivUnLike, "scaleY", 0, 1, 0.8f, 1).setDuration(500);

        animatorSet.playTogether(ivUnLikeScaleX, ivUnLikeScaleY);
        animatorSet.start();
        new Handler().postDelayed(() -> {
            ivUnLikeScaleX = null;
            ivUnLikeScaleY = null;
        }, 500);
    }

    public void playLike() {
        mBinding.ivUnLike.setVisibility(GONE);
        mBinding.ivCircle.setVisibility(VISIBLE);
        mBinding.ivLike.setVisibility(VISIBLE);
        ivCircleScaleX = ObjectAnimator.ofFloat(mBinding.ivCircle, "scaleX", 0, 0.8f).setDuration(500);
        ivCircleScaleY = ObjectAnimator.ofFloat(mBinding.ivCircle, "scaleY", 0, 0.8f).setDuration(500);

        ivLikeScaleX = ObjectAnimator.ofFloat(mBinding.ivLike, "scaleX", 0, 1, 0.8f, 1).setDuration(500);
        ivLikeScaleY = ObjectAnimator.ofFloat(mBinding.ivLike, "scaleY", 0, 1, 0.8f, 1).setDuration(500);

        animatorSet.playTogether(ivCircleScaleX, ivCircleScaleY, ivLikeScaleX, ivLikeScaleY);
        animatorSet.start();

        new Handler().postDelayed(() -> {
            ivCircleScaleX = null;
            ivCircleScaleY = null;
            ivLikeScaleX = null;
            ivLikeScaleY = null;
            mBinding.ivCircle.setVisibility(GONE);
        }, 500);
    }


    @BindingAdapter("isLike")
    public static void likeStatus(GoodView view, boolean isLike) {
        if (isLike) {
            view.findViewById(R.id.iv_like).setVisibility(VISIBLE);
            view.findViewById(R.id.iv_unLike).setVisibility(GONE);
        } else {
            view.findViewById(R.id.iv_like).setVisibility(GONE);
            view.findViewById(R.id.iv_unLike).setVisibility(VISIBLE);
        }
    }
}
