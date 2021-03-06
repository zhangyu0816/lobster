package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllHistoryMsgApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.NewsManagerVMInterface;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NewsManagerViewModel extends BaseViewModel implements NewsManagerVMInterface {
    private BaseReceiver newsCountReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
            }
        };
    }

    @Override
    public void back(View view) {
        super.back(view);
        try {
            newsCountReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        readNewDynMsgAllApi api = new readNewDynMsgAllApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.mineNewsCount.setFriendDynamicGiftNum(0);
                MineApp.mineNewsCount.setFriendDynamicGoodNum(0);
                MineApp.mineNewsCount.setFriendDynamicReviewNum(0);
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_newsCount"));
                if (MineApp.mineNewsCount.getSystemNewsNum() > 0) {
                    clearAllHistoryMsg(BaseActivity.systemUserId);
                    thirdReadChat(BaseActivity.systemUserId);
                }
            }
        }, activity).setReviewType(0);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toGiftNews(View view) {
        ActivityUtils.getMineNewsList(3);
    }

    @Override
    public void toReviewNews(View view) {
        ActivityUtils.getMineNewsList(1);
    }

    @Override
    public void toLikeNews(View view) {
        ActivityUtils.getMineNewsList(2);
    }

    @Override
    public void toLobsterNews(View view) {
        mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
        ActivityUtils.getMineSystemMsg();
    }

    private void clearAllHistoryMsg(long otherUserId) {
        clearAllHistoryMsgApi api = new clearAllHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.mineNewsCount.setSystemNewsNum(0);
                MineApp.mineNewsCount.setMsgType(0);
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_newsCount"));
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * ??????????????????
     */
    private void thirdReadChat(long otherUserId) {
        thirdReadChatApi api = new thirdReadChatApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
