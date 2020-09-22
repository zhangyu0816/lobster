package com.zb.lib_base.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.zb.lib_base.R;

import androidx.annotation.NonNull;

public class MyHeaderView extends InternalAbstract {
    private ObjectAnimator animator;

    public MyHeaderView(Context context) {
        this(context, null);
    }

    public MyHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.refresh_head, this);
        ImageView progress = view.findViewById(R.id.progress);
        animator = ObjectAnimator.ofFloat(progress, "rotation", 0, 360).setDuration(700);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(Animation.INFINITE);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        animator.cancel();
        super.onFinish(layout, success);
        return 200; //延迟500毫秒之后再弹回
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        animator.start();
    }
}
