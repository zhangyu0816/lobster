package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.NewsManagerVMInterface;

import java.util.List;

import androidx.databinding.ViewDataBinding;

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
        newsCountReceiver.unregisterReceiver();
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
                if (MineApp.mineNewsCount.getSystemNewsNum() > 0) {
                    systemHistoryMsgList();
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
        ActivityUtils.getMineSystemMsg();
    }

    @Override
    public void systemHistoryMsgList() {
        systemHistoryMsgListApi api = new systemHistoryMsgListApi(new HttpOnNextListener<List<SystemMsg>>() {
            @Override
            public void onNext(List<SystemMsg> o) {
                clearHistoryMsg(o.get(0).getId());
            }
        }, activity).setPageNo(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void clearHistoryMsg(long messageId) {
        clearHistoryMsgApi api = new clearHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.mineNewsCount.setSystemNewsNum(0);
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
            }
        }, activity).setMessageId(messageId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
