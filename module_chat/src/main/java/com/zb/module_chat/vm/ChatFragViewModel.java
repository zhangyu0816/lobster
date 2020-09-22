package com.zb.module_chat.vm;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.recommendRankingListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.R;
import com.zb.module_chat.databinding.ChatFragBinding;
import com.zb.module_chat.iv.ChatFragVMInterface;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class ChatFragViewModel extends BaseViewModel implements ChatFragVMInterface {
    private ChatFragBinding mBinding;
    private RecommendInfo recommendInfo;
    private Handler handler = new Handler();
    private int time = 1000 * 60 * 2;
    private ObjectAnimator anim;
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

            Keyframe t1 = Keyframe.ofFloat(0, outWidth);
            Keyframe t2 = Keyframe.ofFloat(0.2f, 0);
            Keyframe t3 = Keyframe.ofFloat(0.9f, 0);
            Keyframe t4 = Keyframe.ofFloat(1f, backWidth);

            PropertyValuesHolder pvhTX = PropertyValuesHolder.ofKeyframe("translationX", t1, t2, t3, t4);
            anim = ObjectAnimator.ofPropertyValuesHolder(mBinding.recommendMainLayout, pvhTX);
            anim.setDuration(1700).start();
            new Handler().postDelayed(() -> {
                if (anim != null)
                    anim.cancel();
                anim = null;
            }, 1700);
            handler.postDelayed(ra, time);
            if (MineApp.recommendInfoList.size() == 0) {
                recommendRankingList();
            }
        }
    };
    private ObjectAnimator outOutX;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver bottleTitleReceiver;
    private BaseReceiver updateRedReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (ChatFragBinding) binding;
        mBinding.setIsPlay(false);
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

        bottleTitleReceiver = new BaseReceiver(activity, "lobster_bottleTitle") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setIsPlay(intent.getBooleanExtra("isPlay", false));
            }
        };
        updateRedReceiver = new BaseReceiver(activity, "lobster_updateRed") {
            @Override
            public void onReceive(Context context, Intent intent) {
                String temp = "聊天-" + (ChatListDb.getInstance().getChatTabRed() > 0 ? "true" : "false");
                initTabLayout(new String[]{"所有匹配", temp}, mBinding.tabLayout, mBinding.viewPage, R.color.black_252, R.color.black_827, MineApp.chatSelectIndex);
            }
        };
        recommendRankingList();
    }

    public void onDestroy() {
        try {
            updateRedReceiver.unregisterReceiver();
            newsCountReceiver.unregisterReceiver();
            bottleTitleReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void toGift(View view) {
        ActivityUtils.getMineNewsList(3);
    }

    @Override
    public void toReview(View view) {
        ActivityUtils.getMineNewsList(1);
    }

    @Override
    public void toGood(View view) {
        ActivityUtils.getMineNewsList(2);
    }

    @Override
    public void toService(View view) {
        ActivityUtils.getMineSystemMsg();
    }

    @Override
    public void recommendRankingList() {
        recommendRankingListApi api = new recommendRankingListApi(new HttpOnNextListener<List<RecommendInfo>>() {
            @Override
            public void onNext(List<RecommendInfo> o) {
                MineApp.recommendInfoList.addAll(o);
            }
        }, activity).setCityId(AreaDb.getInstance().getCityId(PreferenceUtil.readStringValue(activity, "cityName"))).setSex(MineApp.mineInfo.getSex() == 0 ? 1 : 0);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
