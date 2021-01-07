package com.zb.module_bottle.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
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

public class BottleBGView extends RelativeLayout {

    private ImageView lsxq;
    private ImageView hd_s;
    private ImageView plp_d;
    private ImageView bl;
    private ImageView plp;
    private ImageView hd_q;
    private ImageView jg;
    private ImageView xx;
    private ImageView fsxq;
    private ImageView ivWang;
    private ImageView ivWangBack;
    private ImageView ivBottle;
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
        ImageView fsxq_temp = findViewById(R.id.fsxq_temp);
        ImageView dg = findViewById(R.id.dg);
        ImageView deng = findViewById(R.id.deng);
        hd_s = findViewById(R.id.hd_s);
        plp_d = findViewById(R.id.plp_d);
        bl = findViewById(R.id.bl);
        plp = findViewById(R.id.plp);
        hd_q = findViewById(R.id.hd_q);
        jg = findViewById(R.id.jg);
        xx = findViewById(R.id.xx);
        ImageView xy = findViewById(R.id.xy);
        fsxq = findViewById(R.id.fsxq);
        ivWang = findViewById(R.id.iv_wang);
        ivWangBack = findViewById(R.id.iv_wang_back);
        ivBottle = findViewById(R.id.iv_bottle);
        ImageView yy = findViewById(R.id.yinying);
        fsxq.setVisibility(GONE);

        lsxq.setBackgroundResource(R.mipmap.lansexingqiu);
        fsxq_temp.setBackgroundResource(R.mipmap.bottle_star_icon);
        ivBottle.setBackgroundResource(R.mipmap.bottle_info_icon);
        dg.setBackgroundResource(R.mipmap.dengtadeguang);
        deng.setBackgroundResource(R.mipmap.dengta);
        ivWang.setBackgroundResource(R.mipmap.wang_icon);
        ivWangBack.setBackgroundResource(R.mipmap.wang_back_icon);
        hd_s.setBackgroundResource(R.mipmap.haidi1);
        plp.setBackgroundResource(R.mipmap.plp);
        hd_q.setBackgroundResource(R.mipmap.haidi);
        jg.setBackgroundResource(R.mipmap.jg);
        plp_d.setBackgroundResource(R.mipmap.plp_d);
        xy.setBackgroundResource(R.mipmap.xingyun);
        xx.setBackgroundResource(R.mipmap.xx);
        fsxq.setBackgroundResource(R.mipmap.bottle_star_icon);
        bl.setBackgroundResource(R.mipmap.bolan);
        yy.setBackgroundResource(R.mipmap.yinying);
        postDelayed(() -> fsxq.setVisibility(VISIBLE), 1300);
    }

    private ObjectAnimator anim_lsxq, anim_xx, anim_fsxq, anim_hd_s, anim_plp_d, anim_bl;
    private PropertyValuesHolder pvhTY;
    private PropertyValuesHolder pvhTX;
    private PropertyValuesHolder pvhR;
    private ObjectAnimator pvh_hd_q, pvh_jg, pvh_plp;

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

        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0f, 30f, 0f);
        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        pvh_hd_q = ObjectAnimator.ofPropertyValuesHolder(hd_q, pvhTY, pvhA).setDuration(4000);
        pvh_hd_q.setRepeatCount(Animation.INFINITE);


        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0f, -30f, 0f);
        pvhTX = PropertyValuesHolder.ofFloat("translationX", 0f, 30f, 0f);
        pvh_jg = ObjectAnimator.ofPropertyValuesHolder(jg, pvhTY, pvhTX, pvhA).setDuration(4000);
        pvh_jg.setRepeatCount(Animation.INFINITE);

        pvh_plp = ObjectAnimator.ofPropertyValuesHolder(plp, pvhTY, pvhTX).setDuration(4000);
        pvh_plp.setRepeatCount(Animation.INFINITE);

        set.playTogether(anim_lsxq,
                anim_xx, anim_fsxq,
                anim_hd_s,
                anim_plp_d,
                anim_bl);
    }

    public void startBg() {
        if (set != null)
            set.start();
        if (pvh_hd_q != null)
            pvh_hd_q.start();
        if (pvh_jg != null)
            pvh_jg.start();
        if (pvh_plp != null)
            pvh_plp.start();
    }

    public void stopBg() {
        if (set != null)
            set.cancel();
        if (pvh_hd_q != null)
            pvh_hd_q.cancel();
        if (pvh_jg != null)
            pvh_jg.cancel();
        if (pvh_plp != null)
            pvh_plp.cancel();
    }

    public void setDestroy() {
        anim_lsxq = null;
        anim_xx = null;
        anim_fsxq = null;
        anim_hd_s = null;
        anim_plp_d = null;
        anim_bl = null;
        pvh_hd_q = null;
        pvh_jg = null;
        pvh_plp = null;
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

        postDelayed(() -> {
            ivWangBack.setVisibility(View.VISIBLE);
            ivWang.setVisibility(View.GONE);
        }, 2000);
        postDelayed(callBack::success, 2500);
        postDelayed(() -> {
            ivWangBack.setVisibility(View.GONE);
            translateY = null;
            translateX = null;
            translateBackY = null;
            translateBackX = null;
            translateBackY2 = null;
            animatorSet = null;
        }, 3200);
    }

    private ObjectAnimator pvh;

    public void throwBottle(CallBack callBack) {
        ivBottle.setVisibility(VISIBLE);
        int time = 1000;
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0, -ObjectUtils.getViewSizeByWidthFromMax(400), MineApp.H);
        pvhTX = PropertyValuesHolder.ofFloat("translationX", 0, -(MineApp.W / 2f), -MineApp.W);
        pvhR = PropertyValuesHolder.ofFloat("rotation", 0, -900);
        pvh = ObjectAnimator.ofPropertyValuesHolder(ivBottle, pvhTY, pvhTX, pvhR).setDuration(time);
        pvh.start();

        postDelayed(() -> {
            pvhTY = null;
            pvhTX = null;
            pvhR = null;
            pvh = null;
            callBack.success();
            ivBottle.setVisibility(GONE);
        }, time);
    }

    public interface CallBack {
        void success();
    }
}
