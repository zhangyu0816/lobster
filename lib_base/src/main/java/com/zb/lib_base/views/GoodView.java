package com.zb.lib_base.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.zb.lib_base.R;
import com.zb.lib_base.databinding.GoodLayoutBinding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class GoodView extends RelativeLayout {
    private GoodLayoutBinding mBinding;

    private PropertyValuesHolder pvhSY, pvhSX;
    private ObjectAnimator pvh_dislike, pvh_circle, pvh_like;

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
    }

    public void playUnlike() {
        mBinding.ivUnLike.setVisibility(VISIBLE);
        mBinding.ivCircle.setVisibility(GONE);
        mBinding.ivLike.setVisibility(GONE);

        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1, 0.8f, 1);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1, 0.8f, 1);
        pvh_dislike = ObjectAnimator.ofPropertyValuesHolder(mBinding.ivUnLike, pvhSY, pvhSX).setDuration(500);
        pvh_dislike.start();

        postDelayed(() -> {
            if (pvh_dislike != null)
                pvh_dislike.cancel();
            pvh_dislike = null;
        }, 500);
    }

    public void playLike() {
        mBinding.ivUnLike.setVisibility(GONE);
        mBinding.ivCircle.setVisibility(VISIBLE);
        mBinding.ivLike.setVisibility(VISIBLE);

        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 0.8f);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 0.8f);
        pvh_circle = ObjectAnimator.ofPropertyValuesHolder(mBinding.ivCircle, pvhSY, pvhSX).setDuration(500);
        pvh_circle.start();

        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1, 0.8f, 1);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1, 0.8f, 1);
        pvh_like = ObjectAnimator.ofPropertyValuesHolder(mBinding.ivLike, pvhSY, pvhSX).setDuration(500);
        pvh_like.start();

        postDelayed(() -> {
            if (pvh_circle != null) {
                pvh_circle.cancel();
            }
            pvh_circle = null;
            if (pvh_like != null)
                pvh_like.cancel();
            pvh_like = null;
            mBinding.ivCircle.setVisibility(GONE);
        }, 500);
    }


    @BindingAdapter(value = {"isLike", "isGrey"}, requireAll = false)
    public static void likeStatus(GoodView view, boolean isLike, boolean isGrey) {
        view.findViewById(R.id.iv_unLike).setBackgroundResource(isGrey ? R.drawable.like_unselect_grey_icon : R.drawable.like_unselect_icon);
        if (isLike) {
            view.findViewById(R.id.iv_like).setVisibility(VISIBLE);
            view.findViewById(R.id.iv_unLike).setVisibility(GONE);
        } else {
            view.findViewById(R.id.iv_like).setVisibility(GONE);
            view.findViewById(R.id.iv_unLike).setVisibility(VISIBLE);
        }
    }
}
