package com.zb.module_bottle.windows;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zb.module_bottle.R;

import androidx.annotation.Nullable;

/**
 * @author: gq.xie
 * @date: 2020/8/7 0007
 * @describe:
 */
public class BottleBGView extends LinearLayout {

    private ImageView lsxq, hd_s, plp_d, bl, dg, plp, hd_q, jg, xx, fsxq;

    public BottleBGView(Context context) {
        super(context);
        init(context);
    }

    public BottleBGView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BottleBGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bottle_bg, null);
        addView(view);

        findViewId();
        setAnim();
    }

    private void findViewId() {
        lsxq = findViewById(R.id.lsxq);
        hd_s = findViewById(R.id.hd_s);
        plp_d = findViewById(R.id.plp_d);
        bl = findViewById(R.id.bl);
        dg = findViewById(R.id.dg);
        plp = findViewById(R.id.plp);
        hd_q = findViewById(R.id.hd_q);
        jg = findViewById(R.id.jg);
        xx = findViewById(R.id.xx);
        fsxq = findViewById(R.id.fsxq);
    }

    private void setAnim() {
        //蓝色星球
        ObjectAnimator anim_lsxq =
                ObjectAnimator.ofFloat(lsxq, "translationY", 0f, 50f, 0f);
        anim_lsxq.setDuration(5000);
        anim_lsxq.setRepeatCount(Animation.INFINITE);

//        ObjectAnimator anim_dg =
//                ObjectAnimator.ofFloat(dg, "rotation", 0f, 45f, 0f)
//                .setDuration(3000);
//        anim_dg.setRepeatCount(Animation.INFINITE);
//        anim_dg.start();

        //星星
        ObjectAnimator anim_xx =
                ObjectAnimator.ofFloat(xx, "alpha", 1f, 0.5f, 1f)
                        .setDuration(2000);
        anim_xx.setRepeatCount(Animation.INFINITE);

        //粉色星球
        ObjectAnimator anim_fsxq =
                ObjectAnimator.ofFloat(fsxq, "translationY", 0f, -50f, 0f)
                        .setDuration(4000);
        anim_fsxq.setRepeatCount(Animation.INFINITE);

        //
        ObjectAnimator anim_hd_s =
                ObjectAnimator.ofFloat(hd_s, "translationY", 0f, 30f, 0f)
                        .setDuration(4000);
        anim_hd_s.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_plp_d =
                ObjectAnimator.ofFloat(plp_d, "translationY", 0f, 30f, 0f)
                        .setDuration(4000);
        anim_plp_d.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_bl =
                ObjectAnimator.ofFloat(bl, "translationY", 0f, 30f, 0f)
                        .setDuration(4000);
        anim_bl.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_hd_q_translationY =
                ObjectAnimator.ofFloat(hd_q, "translationY", 0f, 30f, 0f)
                        .setDuration(4000);
        anim_hd_q_translationY.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_hd_q_alpha =
                ObjectAnimator.ofFloat(hd_q, "alpha", 1f, 0f, 1f)
                        .setDuration(4000);
        anim_hd_q_alpha.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_jg_alpha =
                ObjectAnimator.ofFloat(jg, "alpha", 1f, 0f, 1f)
                        .setDuration(4000);
        anim_jg_alpha.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_jg_translationY =
                ObjectAnimator.ofFloat(jg, "translationY", 0f, -30f, 0f)
                        .setDuration(4000);
        anim_jg_translationY.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_jg_translationX =
                ObjectAnimator.ofFloat(jg, "translationX", 0f, 30f, 0f)
                        .setDuration(4000);
        anim_jg_translationX.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_plp_translationY =
                ObjectAnimator.ofFloat(plp, "translationY", 0f, -30f, 0f)
                        .setDuration(4000);
        anim_plp_translationY.setRepeatCount(Animation.INFINITE);

        ObjectAnimator anim_plp_translationX =
                ObjectAnimator.ofFloat(plp, "translationX", 0f, 30f, 0f)
                        .setDuration(4000);
        anim_plp_translationX.setRepeatCount(Animation.INFINITE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim_lsxq,
                anim_xx, anim_fsxq,
                anim_hd_s,
                anim_plp_d,
                anim_bl,
                anim_hd_q_translationY,
                anim_hd_q_alpha,
                anim_jg_alpha,
                anim_jg_translationX,
                anim_jg_translationY,
                anim_plp_translationY,
                anim_plp_translationX);
        set.start();
    }
}
