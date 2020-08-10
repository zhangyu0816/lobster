package com.zb.module_bottle.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.findBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.databinding.BottleThrowBinding;
import com.zb.module_bottle.iv.BottleThrowVMInterface;

import androidx.databinding.ViewDataBinding;

public class BottleThrowViewModel extends BaseViewModel implements BottleThrowVMInterface {
    private BottleThrowBinding mBinding;
    private BaseReceiver updateContactNumReceiver;
    private MediaPlayer mPlayer;
    private AnimatorSet animatorSet = new AnimatorSet();
    private long time = 1500;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleThrowBinding) binding;
        updateContactNumReceiver = new BaseReceiver(activity, "lobster_updateContactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int chatType = intent.getIntExtra("chatType", 0);
                if (chatType == 2) {
                    mBinding.setVariable(BR.noReadNum, MineApp.noReadBottleNum);
                }
            }
        };

        mPlayer = MediaPlayer.create(activity, R.raw.sea_wave);
        new Handler().postDelayed(() -> appSound(), 200);

        ObjectAnimator translateX = ObjectAnimator.ofFloat(mBinding.ivStar, "translationX", 0, MineApp.W - ObjectUtils.getViewSizeByWidthFromMax(559)).setDuration(time);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBinding.ivBg, "scaleX", 1, 1.5f).setDuration(time);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBinding.ivBg, "scaleY", 1, 1.5f).setDuration(time);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.firstLayout, "alpha", 1, 0).setDuration(time);

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY, translateX, alpha);//同时执行
        animatorSet.start();

        new Handler().postDelayed(() -> {
            mBinding.firstLayout.setVisibility(View.GONE);
        }, time);
    }

    public void onDestroy() {
        updateContactNumReceiver.unregisterReceiver();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    public void onResume() {
        if (!mPlayer.isPlaying())
            appSound();
    }

    private void appSound() {
        // 播放声音
        try {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void collectBottle(View view) {
        findBottle();
    }

    @Override
    public void throwBottle(View view) {
        openBottle();
        ActivityUtils.getBottleContent(new BottleInfo());
    }

    @Override
    public void myBottle(View view) {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        openBottle();
        ActivityUtils.getBottleList();
    }

    @Override
    public void findBottle() {
        findBottleApi api = new findBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                openBottle();
                ActivityUtils.getBottleContent(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
