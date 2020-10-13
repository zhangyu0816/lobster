package com.zb.lib_base.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.ViewFlashChatBinding;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.windows.FlashChatPW;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class FlashChat extends LinearLayout {
    private ViewFlashChatBinding mBinding;
    private int maxSize = 0;
    private Handler handler = new Handler();
    private long time = 3 * 1000;
    private static PropertyValuesHolder pvhTX, pvhSX, pvhSY;
    private ObjectAnimator pvh_logo1;
    private ObjectAnimator pvh_logo2;
    private int index = 2;

    private Runnable ra = new Runnable() {
        @Override
        public void run() {
            index = (index + 1) % maxSize;
            pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1, 0);
            pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1, 0);
            pvh_logo1 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout1, pvhSY, pvhSX).setDuration(300);
            pvh_logo1.start();

            pvhTX = PropertyValuesHolder.ofFloat("translationX", 0, -DisplayUtils.dip2px(30));
            pvh_logo2 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout2, pvhTX).setDuration(600);
            pvh_logo2.start();

            pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1);
            pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1);
            ObjectAnimator pvh_logo3 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout3, pvhSY, pvhSX).setDuration(600);
            pvh_logo3.start();

            new Handler().postDelayed(() -> mBinding.logoLayout1.setVisibility(View.GONE), 300);

            new Handler().postDelayed(() -> {
                mBinding.setRecommendInfo1(mBinding.getRecommendInfo2());
                pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                pvh_logo1 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout1, pvhSY, pvhSX).setDuration(100);
                pvh_logo1.start();
            }, 600);

            new Handler().postDelayed(() -> {
                mBinding.logoLayout2.setVisibility(View.GONE);
                mBinding.logoLayout1.setVisibility(View.VISIBLE);
                pvhTX = PropertyValuesHolder.ofFloat("translationX", -DisplayUtils.dip2px(30), 0);
                pvh_logo2 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout2, pvhTX).setDuration(200);
                pvh_logo2.start();
            }, 700);

            new Handler().postDelayed(() -> {
                mBinding.setRecommendInfo2(mBinding.getRecommendInfo3());
                mBinding.logoLayout2.setVisibility(View.VISIBLE);
                mBinding.setRecommendInfo3(MineApp.recommendInfoList.get(index));
            }, 900);
            handler.postDelayed(ra, time);
        }
    };

    public FlashChat(Context context) {
        super(context);
        init(context);
    }

    public FlashChat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlashChat(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("CheckResult")
    public void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_flash_chat, null, false);
        addView(mBinding.getRoot());

        RxView.clicks(mBinding.flashLayout)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> new FlashChatPW(mBinding.getRoot(),mBinding.getRecommendInfo1()));
    }

    public void initData() {
        maxSize = MineApp.recommendInfoList.size();
        if (maxSize == 1) {
            mBinding.setRecommendInfo1(MineApp.recommendInfoList.get(0));
            mBinding.logoLayout2.setVisibility(View.GONE);
            mBinding.logoLayout3.setVisibility(View.GONE);
        } else if (maxSize == 2) {
            mBinding.setRecommendInfo1(MineApp.recommendInfoList.get(0));
            mBinding.setRecommendInfo2(MineApp.recommendInfoList.get(1));
            mBinding.logoLayout3.setVisibility(View.GONE);
        } else {
            mBinding.setRecommendInfo1(MineApp.recommendInfoList.get(index - 2));
            mBinding.setRecommendInfo2(MineApp.recommendInfoList.get(index - 1));
            mBinding.setRecommendInfo3(MineApp.recommendInfoList.get(index));
            handler.postDelayed(ra, time);
        }


    }

}
