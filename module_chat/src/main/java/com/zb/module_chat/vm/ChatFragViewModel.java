package com.zb.module_chat.vm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.databinding.ChatFragBinding;
import com.zb.module_chat.iv.ChatFragVMInterface;

import androidx.databinding.ViewDataBinding;

public class ChatFragViewModel extends BaseViewModel implements ChatFragVMInterface {
    private ChatFragBinding mBinding;
    private RecommendInfo recommendInfo;
    private Handler handler = new Handler();
    private int time = 1000 * 60 * 2;
    private ObjectAnimator outX, backX;
    private AnimatorSet animatorSet = new AnimatorSet();
    private Runnable ra = new Runnable() {
        @Override
        public void run() {
            if (MineApp.recommendInfoList.size() == 0)
                return;
            mBinding.recommendMainLayout.setVisibility(View.VISIBLE);
            recommendInfo = MineApp.recommendInfoList.remove(0);
            mBinding.setRecommendInfo(recommendInfo);
            int outWidth = mBinding.recommendMainLayout.getWidth();
            int backWidth = mBinding.recommendLayout.getWidth();

            outX = ObjectAnimator.ofFloat(mBinding.recommendMainLayout, "translationX", outWidth, 0).setDuration(500);
            backX = ObjectAnimator.ofFloat(mBinding.recommendMainLayout, "translationX", 0, backWidth).setDuration(200);

            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.play(outX);
            animatorSet.play(backX).after(outX).after(1000);
            animatorSet.start();

            handler.postDelayed(ra, time);
            if (MineApp.recommendInfoList.size() == 0) {
                activity.sendBroadcast(new Intent("lobster_recommend"));
            }
        }
    };
    private ObjectAnimator outOutX;
    private BaseReceiver newsCountReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (ChatFragBinding) binding;
        handler.postDelayed(ra, time);

        new Handler().postDelayed(() -> {
            int outWidth = mBinding.recommendMainLayout.getWidth();
            outOutX = ObjectAnimator.ofFloat(mBinding.recommendMainLayout, "translationX", 0, outWidth).setDuration(100);
            outOutX.start();
        }, 200);

        mBinding.setMineNewsCount(MineApp.mineNewsCount);

        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setMineNewsCount(MineApp.mineNewsCount);
            }
        };
    }

    public void onDestroy() {
        newsCountReceiver.unregisterReceiver();
    }

    @Override
    public void entryBottle(View view) {
        ActivityUtils.getBottleThrow();
    }

    @Override
    public void toMemberInfo(View view) {
        mBinding.recommendMainLayout.setVisibility(View.GONE);
        ActivityUtils.getCardMemberDetail(recommendInfo.getUserId(), false);
    }

    @Override
    public void toNews(View view) {
        ActivityUtils.getMineNewsManager();
    }
}
