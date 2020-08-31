package com.zb.module_bottle.windows;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.module_bottle.R;

import androidx.annotation.Nullable;

/**
 * @author: gq.xie
 * @date: 2020/8/7 0007
 * @describe:
 */
public class BottleBGView extends RelativeLayout {

    private ImageView lsxq, hd_s, plp_d, bl, dg, plp, hd_q, jg, xx, fsxq, ivWang, ivWangBack, ivBottle;
    private AnimatorSet set = new AnimatorSet();

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
        ivWang = findViewById(R.id.iv_wang);
        ivWangBack = findViewById(R.id.iv_wang_back);
        ivBottle = findViewById(R.id.iv_bottle);
        fsxq.setVisibility(GONE);
        new Handler().postDelayed(() -> fsxq.setVisibility(VISIBLE), 1300);
    }

    private ObjectAnimator anim_lsxq, anim_xx, anim_fsxq, anim_hd_s, anim_plp_d, anim_bl, anim_hd_q_translationY, anim_hd_q_alpha, anim_jg_alpha,
            anim_jg_translationY, anim_jg_translationX, anim_plp_translationY, anim_plp_translationX;

    private void setAnim() {
        //蓝色星球
        anim_lsxq = ObjectAnimator.ofFloat(lsxq, "translationY", 0f, 50f, 0f);
        anim_lsxq.setDuration(5000);
        anim_lsxq.setRepeatCount(Animation.INFINITE);

        //星星
        anim_xx = ObjectAnimator.ofFloat(xx, "alpha", 1f, 0.5f, 1f)
                .setDuration(2000);
        anim_xx.setRepeatCount(Animation.INFINITE);

        //粉色星球
        anim_fsxq = ObjectAnimator.ofFloat(fsxq, "translationY", 0f, -50f, 0f)
                .setDuration(4000);
        anim_fsxq.setRepeatCount(Animation.INFINITE);

        //
        anim_hd_s = ObjectAnimator.ofFloat(hd_s, "translationY", 0f, 30f, 0f)
                .setDuration(4000);
        anim_hd_s.setRepeatCount(Animation.INFINITE);

        anim_plp_d = ObjectAnimator.ofFloat(plp_d, "translationY", 0f, 30f, 0f)
                .setDuration(4000);
        anim_plp_d.setRepeatCount(Animation.INFINITE);

        anim_bl = ObjectAnimator.ofFloat(bl, "translationY", 0f, 30f, 0f)
                .setDuration(4000);
        anim_bl.setRepeatCount(Animation.INFINITE);

        anim_hd_q_translationY = ObjectAnimator.ofFloat(hd_q, "translationY", 0f, 30f, 0f)
                .setDuration(4000);
        anim_hd_q_translationY.setRepeatCount(Animation.INFINITE);

        anim_hd_q_alpha = ObjectAnimator.ofFloat(hd_q, "alpha", 1f, 0f, 1f)
                .setDuration(4000);
        anim_hd_q_alpha.setRepeatCount(Animation.INFINITE);

        anim_jg_alpha = ObjectAnimator.ofFloat(jg, "alpha", 1f, 0f, 1f)
                .setDuration(4000);
        anim_jg_alpha.setRepeatCount(Animation.INFINITE);

        anim_jg_translationY = ObjectAnimator.ofFloat(jg, "translationY", 0f, -30f, 0f)
                .setDuration(4000);
        anim_jg_translationY.setRepeatCount(Animation.INFINITE);

        anim_jg_translationX = ObjectAnimator.ofFloat(jg, "translationX", 0f, 30f, 0f)
                .setDuration(4000);
        anim_jg_translationX.setRepeatCount(Animation.INFINITE);

        anim_plp_translationY = ObjectAnimator.ofFloat(plp, "translationY", 0f, -30f, 0f)
                .setDuration(4000);
        anim_plp_translationY.setRepeatCount(Animation.INFINITE);

        anim_plp_translationX = ObjectAnimator.ofFloat(plp, "translationX", 0f, 30f, 0f)
                .setDuration(4000);
        anim_plp_translationX.setRepeatCount(Animation.INFINITE);


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
    }

    public void startBg() {
        set.start();
    }

    public void stopBg() {
        set.cancel();
    }

    public void setDestroy() {
        anim_lsxq = null;
        anim_xx = null;
        anim_fsxq = null;
        anim_hd_s = null;
        anim_plp_d = null;
        anim_bl = null;
        anim_hd_q_translationY = null;
        anim_hd_q_alpha = null;
        anim_jg_alpha = null;
        anim_jg_translationY = null;
        anim_jg_translationX = null;
        anim_plp_translationY = null;
        anim_plp_translationX = null;
        set = null;
    }

    private ObjectAnimator translateY, translateX, translateBackY, translateBackX, translateBackY2;
    private AnimatorSet animatorSet = null;

    public void startWang(CallBack callBack) {
        animatorSet = new AnimatorSet();
        ivWang.setVisibility(View.VISIBLE);
        translateY = ObjectAnimator.ofFloat(ivWang, "translationY", 0, ObjectUtils.getViewSizeByWidthFromMax(600)).setDuration(1000);
        translateX = ObjectAnimator.ofFloat(ivWang, "translationX", 0, ObjectUtils.getViewSizeByWidthFromMax(100), 0).setDuration(1000);
        translateBackY = ObjectAnimator.ofFloat(ivWangBack, "translationY", 0, ObjectUtils.getViewSizeByWidthFromMax(600)).setDuration(1000);
        translateBackX = ObjectAnimator.ofFloat(ivWangBack, "translationX", 0, ObjectUtils.getViewSizeByWidthFromMax(100), 0).setDuration(1000);
        translateBackY2 = ObjectAnimator.ofFloat(ivWangBack, "translationY", ObjectUtils.getViewSizeByWidthFromMax(600), 0).setDuration(1000);

        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(translateY).with(translateBackY);
        animatorSet.play(translateX).with(translateBackX).after(translateY);
        animatorSet.play(translateBackY2).after(translateX);
        animatorSet.start();

        new Handler().postDelayed(() -> {
            ivWangBack.setVisibility(View.VISIBLE);
            ivWang.setVisibility(View.GONE);
        }, 2000);
        new Handler().postDelayed(() -> {
            ivWangBack.setVisibility(View.GONE);
            translateY = null;
            translateX = null;
            translateBackY = null;
            translateBackX = null;
            translateBackY2 = null;
            animatorSet = null;
            callBack.success();
        }, 3000);
    }

    private ObjectAnimator translateBottleY, translateBottleX, animatorBottle;
    private AnimatorSet animatorSet1 = null;

    public void throwBottle(CallBack callBack) {
        ivBottle.setVisibility(VISIBLE);
        animatorSet1 = new AnimatorSet();
        int time = 1000;
        translateBottleY = ObjectAnimator.ofFloat(ivBottle, "translationY", 0, -ObjectUtils.getViewSizeByWidthFromMax(400), MineApp.H).setDuration(time);
        translateBottleX = ObjectAnimator.ofFloat(ivBottle, "translationX", 0, -(MineApp.W / 2), -MineApp.W).setDuration(time);
        animatorBottle = ObjectAnimator.ofFloat(ivBottle, "rotation", 0, -900).setDuration(time);

        animatorSet1.setInterpolator(new LinearInterpolator());
        animatorSet1.play(translateBottleY).with(translateBottleX).with(animatorBottle);
        animatorSet1.start();

        new Handler().postDelayed(() -> {
            translateBottleY = null;
            translateBottleX = null;
            animatorBottle = null;
            animatorSet1 = null;
            callBack.success();
        }, time);
    }

    public interface CallBack {
        void success();
    }
}
