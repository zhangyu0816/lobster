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
import com.zb.lib_base.api.castBottleApi;
import com.zb.lib_base.api.findBottleApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.pickBottleApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.databinding.BottleThrowBinding;
import com.zb.module_bottle.iv.BottleThrowVMInterface;

import androidx.databinding.ViewDataBinding;

public class BottleThrowViewModel extends BaseViewModel implements BottleThrowVMInterface {
    private BottleThrowBinding mBinding;
    private BaseReceiver updateContactNumReceiver;
    private BaseReceiver closeSoundReceiver;
    private BaseReceiver resumeSoundReceiver;
    private MediaPlayer mPlayer;
    private AnimatorSet animatorSet = new AnimatorSet();
    private long time = 1500;

    private BottleInfo bottleInfo;
    private boolean isFirst = true;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleThrowBinding) binding;
        mBinding.setIsBottle(false);
        mBinding.setShowBtn(true);
        mBinding.edContent.setTypeface(MineApp.QingSongShouXieTiType);
        mBinding.setMemberInfo(new MemberInfo());
        mBinding.setInfo("");
        updateContactNumReceiver = new BaseReceiver(activity, "lobster_updateContactNum") {
            @Override
            public void onReceive(Context context, Intent intent) {
                int chatType = intent.getIntExtra("chatType", 0);
                if (chatType == 2) {
                    mBinding.setVariable(BR.noReadNum, MineApp.noReadBottleNum);
                }
            }
        };

        closeSoundReceiver = new BaseReceiver(activity, "lobster_closeSound") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    mPlayer.stop();
                }
            }
        };
        resumeSoundReceiver = new BaseReceiver(activity, "lobster_resumeSound") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onResume();
            }
        };

        mPlayer = MediaPlayer.create(activity, R.raw.sea_wave);
        new Handler().postDelayed(() -> appSound(), 200);

        ObjectAnimator translateX = ObjectAnimator.ofFloat(mBinding.ivStar, "translationX", 0, MineApp.W - ObjectUtils.getViewSizeByWidthFromMax(250)).setDuration(time);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBinding.ivBg, "scaleX", 1, 1.5f).setDuration(time);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBinding.ivBg, "scaleY", 1, 1.5f).setDuration(time);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.firstLayout, "alpha", 1, 0).setDuration(time);

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(scaleX, scaleY, translateX, alpha);//同时执行
        animatorSet.start();

        new Handler().postDelayed(() -> {
            mBinding.firstLayout.setVisibility(View.GONE);
            mBinding.bottleWhiteBack.bottleBg.startBg();
        }, time);

    }

    public void onDestroy() {
        updateContactNumReceiver.unregisterReceiver();
        closeSoundReceiver.unregisterReceiver();
        resumeSoundReceiver.unregisterReceiver();
        if (mPlayer != null) {
            mPlayer.stop();
        }
        mBinding.bottleWhiteBack.bottleBg.stopBg();
    }

    public void onResume() {
        if (!mPlayer.isPlaying())
            appSound();
        if (!isFirst) {
            mBinding.bottleWhiteBack.bottleBg.startBg();
        }
        isFirst = false;
    }

    public void onPause() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
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
    public void throwBottle(View view) {
        bottleInfo = new BottleInfo();
        mBinding.setTitle("扔一个瓶子");
        mBinding.setBottleInfo(bottleInfo);
        mBinding.setIsBottle(true);
        mBinding.setShowBtn(false);
        mBinding.edContent.setText("");
        mBinding.edContent.setEnabled(true);
        mBinding.bottleWhiteBack.bottleBg.stopBg();
    }

    @Override
    public void myBottle(View view) {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        mBinding.bottleWhiteBack.bottleBg.stopBg();
        openBottle();
        ActivityUtils.getBottleList();
    }

    @Override
    public void findBottle() {
        findBottleApi api = new findBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                mBinding.setShowBtn(false);
                mBinding.bottleWhiteBack.bottleBg.stopBg();
                bottleInfo = o;
                mBinding.setBottleInfo(bottleInfo);
                otherInfo(bottleInfo.getUserId());
                mBinding.bottleWhiteBack.bottleBg.startWang(() -> {
                    mBinding.edContent.setText(bottleInfo.getText());
                    mBinding.edContent.setEnabled(false);
                    mBinding.setIsBottle(true);
                });
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void otherInfo(long otherUserId) {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                mBinding.setMemberInfo(o);
                mBinding.setInfo((o.getSex() == 0 ? "女 " : "男 ") + o.getAge() + "岁 " + DateUtil.getConstellations(o.getBirthday()));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void sure(View view) {
        if (bottleInfo.getDriftBottleId() == 0) {
            if (mBinding.edContent.getText().toString().trim().isEmpty()) {
                SCToastUtil.showToast(activity, "漂流瓶内容不能为空", true);
                return;
            }
            castBottle();
        } else {
            pickBottle(2);
        }
    }

    @Override
    public void cancel(View view) {
        pickBottle(1);
    }

    @Override
    public void close(View view) {
        hintKeyBoard();
        mBinding.setTitle("我的漂流瓶");
        mBinding.setIsBottle(false);
        mBinding.setShowBtn(true);
        mBinding.bottleWhiteBack.bottleBg.startBg();
    }

    // 创建漂流瓶
    private void castBottle() {
        castBottleApi api = new castBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsBottle(false);
                mBinding.bottleWhiteBack.bottleBg.throwBottle(() -> close(null));
            }
        }, activity).setText(mBinding.edContent.getText().toString());
        HttpManager.getInstance().doHttpDeal(api);
    }

    // //漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
    private void pickBottle(int driftBottleType) {
        pickBottleApi api = new pickBottleApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (driftBottleType == 1) {
                    mBinding.setIsBottle(false);
                    mBinding.bottleWhiteBack.bottleBg.throwBottle(() -> close(null));
                } else if (driftBottleType == 2) {
                    ActivityUtils.getBottleChat(bottleInfo.getDriftBottleId());
                    close(null);
                }
            }
        }, activity).setDriftBottleId(bottleInfo.getDriftBottleId()).setDriftBottleType(driftBottleType);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
