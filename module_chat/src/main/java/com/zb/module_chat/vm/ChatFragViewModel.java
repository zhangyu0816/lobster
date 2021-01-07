package com.zb.module_chat.vm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.flashUserListApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.recommendRankingListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.ChatListDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.FlashInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.R;
import com.zb.module_chat.databinding.ChatFragBinding;
import com.zb.module_chat.iv.ChatFragVMInterface;

import java.util.Iterator;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class ChatFragViewModel extends BaseViewModel implements ChatFragVMInterface {
    private ChatFragBinding mBinding;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver bottleTitleReceiver;
    private BaseReceiver updateRedReceiver;
    private BaseReceiver locationReceiver;
    private BaseReceiver openVipReceiver;
    private BaseReceiver updateFlashReceiver;
    private Handler mHandler;
    private Runnable ra = () -> {
        int height = DisplayUtils.dip2px(30) - mBinding.topLinear.getHeight();
        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                    int y = verticalOffset - height;
                    float alpha = (float) y / DisplayUtils.dip2px(30);
                    if (alpha >= 1f)
                        alpha = 1f;
                    else if (alpha <= 0) {
                        alpha = 0f;
                    }
                    mBinding.viewTop.setAlpha(1 - alpha);
                    mBinding.tvTitle.setAlpha(alpha);
                }
        );
    };

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (ChatFragBinding) binding;
        mBinding.setIsPlay(false);
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

        // 位置漫游
        locationReceiver = new BaseReceiver(activity, "lobster_location") {
            @Override
            public void onReceive(Context context, Intent intent) {
                flashUserList();
            }
        };
        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
        updateFlashReceiver = new BaseReceiver(activity, "lobster_updateFlash") {
            @Override
            public void onReceive(Context context, Intent intent) {
                Iterator<FlashInfo> iterator = MineApp.sFlashInfoList.iterator();
                while (iterator.hasNext()) {
                    FlashInfo item = iterator.next();
                    if (item.getUserId() == MineApp.sFlashInfo.getUserId()) {
                        iterator.remove();
                        mBinding.flashChat.setVisibility(View.VISIBLE);
                        mBinding.flashChat.initData(activity);
                        break;
                    }
                }
            }
        };
        recommendRankingList();
        flashUserList();
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(ra, 300);

    }

    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacks(ra);
        }
        mHandler = null;
        try {
            updateRedReceiver.unregisterReceiver();
            newsCountReceiver.unregisterReceiver();
            bottleTitleReceiver.unregisterReceiver();
            locationReceiver.unregisterReceiver();
            openVipReceiver.unregisterReceiver();
            updateFlashReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void entryBottle(View view) {
        ActivityUtils.getBottleThrow();
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

    @Override
    public void flashUserList() {
        flashUserListApi api = new flashUserListApi(new HttpOnNextListener<List<FlashInfo>>() {
            @Override
            public void onNext(List<FlashInfo> o) {
                MineApp.sFlashInfoList.clear();
                MineApp.sFlashInfoList.addAll(o);
                mBinding.flashChat.setVisibility(View.VISIBLE);
                mBinding.flashChat.initData(activity);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.mineInfo = o;
                activity.sendBroadcast(new Intent("lobster_flashChat"));
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
