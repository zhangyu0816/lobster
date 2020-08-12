package com.zb.module_bottle.windows;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.module_bottle.R;

import androidx.annotation.Nullable;

/**
 * @author: gq.xie
 * @date: 2020/8/7 0007
 * @describe:
 */
public class BottleBGView extends LinearLayout {

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

    private void setAnim() {
        //蓝色星球
        ObjectAnimator anim_lsxq =
                ObjectAnimator.ofFloat(lsxq, "translationY", 0f, 50f, 0f);
        anim_lsxq.setDuration(5000);
        anim_lsxq.setRepeatCount(Animation.INFINITE);

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

    public void startBg(){
        set.start();
    }

    public  void stopBg(){
        set.cancel();
    }

    public void startWang(CallBack callBack) {
        ivWang.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator translateY = ObjectAnimator.ofFloat(ivWang, "translationY", 0, ObjectUtils.getViewSizeByWidthFromMax(600)).setDuration(1000);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(ivWang, "translationX", 0, ObjectUtils.getViewSizeByWidthFromMax(100), 0).setDuration(1000);
        ObjectAnimator translateBackY = ObjectAnimator.ofFloat(ivWangBack, "translationY", 0, ObjectUtils.getViewSizeByWidthFromMax(600)).setDuration(1000);
        ObjectAnimator translateBackX = ObjectAnimator.ofFloat(ivWangBack, "translationX", 0, ObjectUtils.getViewSizeByWidthFromMax(100), 0).setDuration(1000);
        ObjectAnimator translateBackY2 = ObjectAnimator.ofFloat(ivWangBack, "translationY", ObjectUtils.getViewSizeByWidthFromMax(600), 0).setDuration(1000);

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
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
            callBack.success();
        }, 3000);
    }

    public void throwBottle(CallBack callBack) {
        ivBottle.setVisibility(VISIBLE);
        int time = 1000;
        ObjectAnimator translateY = ObjectAnimator.ofFloat(ivBottle, "translationY", 0, -ObjectUtils.getViewSizeByWidthFromMax(400), MineApp.H).setDuration(time);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(ivBottle, "translationX", 0, -(MineApp.W / 2), -MineApp.W).setDuration(time);
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivBottle, "rotation", 0, -900).setDuration(time);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.play(translateY).with(translateX).with(animator);
        animatorSet.start();

        new Handler().postDelayed(() -> callBack.success(), time);
    }

    public interface CallBack {
        void success();
    }
}
