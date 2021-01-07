package com.zb.lib_base.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.ViewFlashChatBinding;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.windows.FlashChatPW;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class FlashChat extends LinearLayout {
    private ViewFlashChatBinding mBinding;
    private int maxSize = 0;
    private long time = 3 * 1000;
    private static PropertyValuesHolder pvhTX, pvhSX, pvhSY;
    private ObjectAnimator pvh_logo1;
    private ObjectAnimator pvh_logo2;
    private ObjectAnimator pvh_logo3;
    private int index = 2;

    private Runnable ra1 = new Runnable() {
        @Override
        public void run() {
            if (mBinding.logoLayout1 != null)
                mBinding.logoLayout1.setVisibility(View.GONE);
        }
    };
    private Runnable ra2 = new Runnable() {
        @Override
        public void run() {
            mBinding.setFlashInfo1(mBinding.getFlashInfo2());
            pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
            pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
            pvh_logo1 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout1, pvhSY, pvhSX).setDuration(100);
            pvh_logo1.start();
        }
    };
    private Runnable ra3 = new Runnable() {
        @Override
        public void run() {
            mBinding.logoLayout2.setVisibility(View.GONE);
            mBinding.logoLayout1.setVisibility(View.VISIBLE);
            pvhTX = PropertyValuesHolder.ofFloat("translationX", -DisplayUtils.dip2px(30), 0);
            pvh_logo2 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout2, pvhTX).setDuration(200);
            pvh_logo2.start();
        }
    };
    private Runnable ra4 = new Runnable() {
        @Override
        public void run() {
            mBinding.setFlashInfo2(mBinding.getFlashInfo3());
            mBinding.logoLayout2.setVisibility(View.VISIBLE);
            if (index >= MineApp.sFlashInfoList.size()) {
                maxSize = MineApp.sFlashInfoList.size();
                index = 0;
            }

            mBinding.setFlashInfo3(MineApp.sFlashInfoList.get(index));
        }
    };

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
            pvh_logo3 = ObjectAnimator.ofPropertyValuesHolder(mBinding.logoLayout3, pvhSY, pvhSX).setDuration(600);
            pvh_logo3.start();

            postDelayed(ra1, 300);
            postDelayed(ra2, 600);
            postDelayed(ra3, 700);
            postDelayed(ra4, 900);

            postDelayed(ra, time);
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
                .subscribe(o -> {
                    MineApp.sFlashInfo = mBinding.getFlashInfo1();
                    new FlashChatPW(mBinding.getRoot());
                });
    }

    public void initData(RxAppCompatActivity activity) {
        maxSize = MineApp.sFlashInfoList.size();
        mBinding.logoLayout2.setVisibility(View.VISIBLE);
        mBinding.logoLayout3.setVisibility(View.VISIBLE);
        index = 2;
        if (pvh_logo1 != null)
            pvh_logo1.cancel();
        if (pvh_logo2 != null)
            pvh_logo2.cancel();
        if (pvh_logo3 != null)
            pvh_logo3.cancel();
        removeCallbacks(ra);
        removeCallbacks(ra1);
        removeCallbacks(ra2);
        removeCallbacks(ra3);
        removeCallbacks(ra4);

        if (maxSize == 1) {
            mBinding.setFlashInfo1(MineApp.sFlashInfoList.get(0));
            mBinding.logoLayout2.setVisibility(View.GONE);
            mBinding.logoLayout3.setVisibility(View.GONE);
        } else if (maxSize == 2) {
            mBinding.setFlashInfo1(MineApp.sFlashInfoList.get(0));
            mBinding.setFlashInfo2(MineApp.sFlashInfoList.get(1));
            mBinding.logoLayout3.setVisibility(View.GONE);
        } else {
            mBinding.setFlashInfo1(MineApp.sFlashInfoList.get(index - 2));
            mBinding.setFlashInfo2(MineApp.sFlashInfoList.get(index - 1));
            mBinding.setFlashInfo3(MineApp.sFlashInfoList.get(index));
            postDelayed(ra, time);
        }
        if (MineApp.mineInfo.getMemberType() == 1 && !TextUtils.equals(PreferenceUtil.readStringValue(activity, "flashChatTime" + BaseActivity.userId), DateUtil.getNow(DateUtil.yyyy_MM_dd))) {
            postDelayed(() -> {
                PreferenceUtil.saveStringValue(activity, "flashChatTime" + BaseActivity.userId, DateUtil.getNow(DateUtil.yyyy_MM_dd));
                MineApp.sFlashInfo = mBinding.getFlashInfo1();
                activity.sendBroadcast(new Intent("lobster_flashChat"));
            }, 3 * 60 * 1000);
        }
    }

}
