package com.zb.lib_base.windows;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.databinding.PwsSuperLikeBinding;


public class SuperLikePW extends BasePopupWindow {

    private String myHead;
    private String otherHead;
    private boolean isPair;
    private int mySex;
    private int otherSex;
    private String otherNick = "";
    private CallBack callBack;
    private PwsSuperLikeBinding binding;

    public SuperLikePW(RxAppCompatActivity activity, View parentView, String myHead, String otherHead, int mySex, int otherSex) {
        super(activity, parentView, false);
        this.myHead = myHead;
        this.otherHead = otherHead;
        this.isPair = false;
        this.mySex = mySex;
        this.otherSex = otherSex;
        initUI();
    }

    public SuperLikePW(RxAppCompatActivity activity, View parentView, String myHead, String otherHead, int mySex, int otherSex, String otherNick, CallBack callBack) {
        super(activity, parentView, false);
        this.myHead = myHead;
        this.otherHead = otherHead;
        this.isPair = true;
        this.mySex = mySex;
        this.otherSex = otherSex;
        this.otherNick = otherNick;
        this.callBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_super_like;
    }

    private ObjectAnimator otherLayoutX, otherLayoutY, mineLayoutX, mineLayoutY, ivSuperLike1X, ivSuperLike1Y, ivSuperLike2X,
            ivSuperLike2Y, scale1X, scale1Y, scale2X, scale2Y;
    private AnimatorSet animatorSet = new AnimatorSet();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initUI() {
        if (!isPair)
            mBinding.getRoot().setOnTouchListener((v, event) -> {
                if (isShowing()) {
                    onDestroy();
                    dismiss();
                }
                return false;
            });
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.myHead, myHead);
        mBinding.setVariable(BR.otherHead, otherHead);
        mBinding.setVariable(BR.isPair, isPair);
        mBinding.setVariable(BR.mySex, mySex);
        mBinding.setVariable(BR.otherSex, otherSex);
        mBinding.setVariable(BR.otherNick, otherNick);
        binding = (PwsSuperLikeBinding) mBinding;
        binding.ivSuperLike1.setVisibility(View.GONE);
        binding.ivSuperLike2.setVisibility(View.GONE);

        if (isPair) {
            binding.ivStar1.setBackgroundResource(R.drawable.like_red_icon);
            binding.ivStar2.setBackgroundResource(R.drawable.like_red_icon);
            binding.ivStar3.setBackgroundResource(R.drawable.like_red_icon);
            binding.ivStar4.setBackgroundResource(R.drawable.like_red_icon);
            binding.ivStar5.setBackgroundResource(R.drawable.like_red_icon);
            binding.ivStar6.setBackgroundResource(R.drawable.like_red_icon);
        }

        int time = 300;
        otherLayoutX = ObjectAnimator.ofFloat(binding.otherLayout, "translationX", -50, 0).setDuration(time);
        otherLayoutY = ObjectAnimator.ofFloat(binding.otherLayout, "translationY", 50, 0).setDuration(time);

        mineLayoutX = ObjectAnimator.ofFloat(binding.mineLayout, "translationX", 50, 0).setDuration(time);
        mineLayoutY = ObjectAnimator.ofFloat(binding.mineLayout, "translationY", 50, 0).setDuration(time);

        ivSuperLike1X = ObjectAnimator.ofFloat(binding.ivSuperLike1, "translationX", -200, 0).setDuration(time);
        ivSuperLike1Y = ObjectAnimator.ofFloat(binding.ivSuperLike1, "translationY", 100, 0).setDuration(time);

        ivSuperLike2X = ObjectAnimator.ofFloat(binding.ivSuperLike2, "translationX", 200, 0).setDuration(time);
        ivSuperLike2Y = ObjectAnimator.ofFloat(binding.ivSuperLike2, "translationY", 100, 0).setDuration(time);

        scale1X = ObjectAnimator.ofFloat(binding.ivSuperLike1, "scaleX", 1, 1.3f, 1, 1.3f, 1).setDuration(time);
        scale1Y = ObjectAnimator.ofFloat(binding.ivSuperLike1, "scaleY", 1, 1.3f, 1, 1.3f, 1).setDuration(time);

        scale2X = ObjectAnimator.ofFloat(binding.ivSuperLike2, "scaleX", 1, 1.3f, 1, 1.3f, 1).setDuration(time);
        scale2Y = ObjectAnimator.ofFloat(binding.ivSuperLike2, "scaleY", 1, 1.3f, 1, 1.3f, 1).setDuration(time);

        if (animatorSet == null)
            return;
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(otherLayoutX).with(otherLayoutY).with(mineLayoutX).with(mineLayoutY);
        animatorSet.play(ivSuperLike1X).with(ivSuperLike1Y).with(ivSuperLike2X).with(ivSuperLike2Y).after(otherLayoutX);
        animatorSet.play(scale1X).with(scale1Y).with(scale2X).with(scale2Y).after(ivSuperLike1X);
        animatorSet.start();

        new Handler().postDelayed(() -> {
            binding.ivSuperLike1.setVisibility(View.VISIBLE);
            binding.ivSuperLike2.setVisibility(View.VISIBLE);
        }, time);
        new Handler().postDelayed(this::start1, time * 3);
    }

    int leftX1 = -100;
    int leftX2 = -400;
    int leftY1 = -500;
    int leftY2 = 600;

    int rightX1 = 100;
    int rightX2 = 400;
    int rightY1 = -500;
    int rightY2 = 600;
    int time = 1500;
    private ObjectAnimator translationX, translationY;
    private AnimatorSet animatorStarSet1 = new AnimatorSet();
    private AnimatorSet animatorStarSet2 = new AnimatorSet();
    private AnimatorSet animatorStarSet3 = new AnimatorSet();
    private AnimatorSet animatorStarSet4 = new AnimatorSet();
    private AnimatorSet animatorStarSet5 = new AnimatorSet();
    private AnimatorSet animatorStarSet6 = new AnimatorSet();


    private void start1() {

        binding.ivStar1.setVisibility(View.VISIBLE);
        translationX = ObjectAnimator.ofFloat(binding.ivStar1, "translationX", 0, leftX1, leftX2).setDuration(time);
        translationY = ObjectAnimator.ofFloat(binding.ivStar1, "translationY", 0, leftY1, leftY2).setDuration(time);
        if (animatorStarSet1 == null)
            return;
        animatorStarSet1.setInterpolator(new LinearInterpolator());
        animatorStarSet1.playTogether(translationX, translationY);
        animatorStarSet1.start();
        new Handler().postDelayed(() -> binding.ivStar1.setVisibility(View.GONE), time);
        new Handler().postDelayed(() -> {
            binding.ivStar2.setVisibility(View.VISIBLE);
            start2();
        }, 200);
    }

    private void start2() {
        binding.ivStar2.setVisibility(View.VISIBLE);
        translationX = ObjectAnimator.ofFloat(binding.ivStar2, "translationX", 0, rightX1 + 100, rightX2 + 350).setDuration(time);
        translationY = ObjectAnimator.ofFloat(binding.ivStar2, "translationY", 0, rightY1 + 100, rightY2 + 160).setDuration(time);
        if (animatorStarSet2 == null)
            return;
        animatorStarSet2.setInterpolator(new LinearInterpolator());
        animatorStarSet2.playTogether(translationX, translationY);
        animatorStarSet2.start();
        new Handler().postDelayed(() -> binding.ivStar2.setVisibility(View.GONE), time);
        new Handler().postDelayed(() -> {
            binding.ivStar3.setVisibility(View.VISIBLE);
            start3();
        }, 200);
    }

    private void start3() {
        binding.ivStar3.setVisibility(View.VISIBLE);
        translationX = ObjectAnimator.ofFloat(binding.ivStar3, "translationX", 0, leftX1 + 50, leftX2 - 150).setDuration(time);
        translationY = ObjectAnimator.ofFloat(binding.ivStar3, "translationY", 0, leftY1 - 150, leftY2 + 150).setDuration(time);
        if (animatorStarSet3 == null)
            return;
        animatorStarSet3.setInterpolator(new LinearInterpolator());
        animatorStarSet3.playTogether(translationX, translationY);
        animatorStarSet3.start();
        new Handler().postDelayed(() -> binding.ivStar3.setVisibility(View.GONE), time);
        new Handler().postDelayed(() -> {
            binding.ivStar4.setVisibility(View.VISIBLE);
            start4();
        }, 200);
    }

    private void start4() {
        binding.ivStar4.setVisibility(View.VISIBLE);
        translationX = ObjectAnimator.ofFloat(binding.ivStar4, "translationX", 0, rightX1 - 60, rightX2 + 90).setDuration(time);
        translationY = ObjectAnimator.ofFloat(binding.ivStar4, "translationY", 0, rightY1 - 160, rightY2 + 190).setDuration(time);
        if (animatorStarSet4 == null)
            return;
        animatorStarSet4.setInterpolator(new LinearInterpolator());
        animatorStarSet4.playTogether(translationX, translationY);
        animatorStarSet4.start();
        new Handler().postDelayed(() -> binding.ivStar4.setVisibility(View.GONE), time);
        new Handler().postDelayed(() -> {
            binding.ivStar5.setVisibility(View.VISIBLE);
            start5();
        }, 200);
    }

    private void start5() {
        binding.ivStar5.setVisibility(View.VISIBLE);
        translationX = ObjectAnimator.ofFloat(binding.ivStar5, "translationX", 0, leftX1 - 70, leftX2 + 170).setDuration(time);
        translationY = ObjectAnimator.ofFloat(binding.ivStar5, "translationY", 0, leftY1 + 180, leftY2 + 100).setDuration(time);
        if (animatorStarSet5 == null)
            return;
        animatorStarSet5.setInterpolator(new LinearInterpolator());
        animatorStarSet5.playTogether(translationX, translationY);
        animatorStarSet5.start();
        new Handler().postDelayed(() -> binding.ivStar5.setVisibility(View.GONE), time);
        new Handler().postDelayed(() -> {
            binding.ivStar6.setVisibility(View.VISIBLE);
            start6();
        }, 200);
    }

    private void start6() {
        binding.ivStar6.setVisibility(View.VISIBLE);
        translationX = ObjectAnimator.ofFloat(binding.ivStar6, "translationX", 0, rightX1 - 40, rightX2 + 180).setDuration(time);
        translationY = ObjectAnimator.ofFloat(binding.ivStar6, "translationY", 0, rightY1 + 60, rightY2 - 90).setDuration(time);
        if (animatorStarSet6 == null)
            return;
        animatorStarSet6.setInterpolator(new LinearInterpolator());
        animatorStarSet6.playTogether(translationX, translationY);
        animatorStarSet6.start();
        new Handler().postDelayed(() -> binding.ivStar6.setVisibility(View.GONE), time);
    }

    public interface CallBack {
        void success();
    }

    public void toChat(View view) {
        onDestroy();
        dismiss();
        if (callBack != null)
            callBack.success();
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        onDestroy();
        dismiss();
    }

    private void onDestroy() {
        otherLayoutX = null;
        otherLayoutY = null;
        mineLayoutX = null;
        mineLayoutY = null;
        ivSuperLike1X = null;
        ivSuperLike1Y = null;
        ivSuperLike2X = null;
        ivSuperLike2Y = null;
        scale1X = null;
        scale1Y = null;
        scale2X = null;
        scale2Y = null;
        translationX = null;
        translationY = null;
        animatorSet = null;
        animatorStarSet1 = null;
        animatorStarSet2 = null;
        animatorStarSet3 = null;
        animatorStarSet4 = null;
        animatorStarSet5 = null;
        animatorStarSet6 = null;
    }
}
