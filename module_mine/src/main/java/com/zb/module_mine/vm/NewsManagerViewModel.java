package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllHistoryMsgApi;
import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.NewsManagerVMInterface;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class NewsManagerViewModel extends BaseViewModel implements NewsManagerVMInterface {
    private BaseReceiver newsCountReceiver;
    private YWConversation conversation;
    private IYWConversationService mConversationService;
    private LoginSampleHelper loginHelper;
    private String otherIMUserId;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
            }
        };
        loginHelper = LoginSampleHelper.getInstance();
        otherImAccountInfoApi();
    }

    @Override
    public void back(View view) {
        super.back(view);
        try {
            mConversationService.markReaded(conversation);
        } catch (Exception e) {
        }
        activity.sendBroadcast(new Intent("lobster_resumeContactNum"));
        newsCountReceiver.unregisterReceiver();
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        new TextPW(activity, mBinding.getRoot(), "清除消息", "清除后，消息无法恢复", "清除", () -> {
            readNewDynMsgAllApi api = new readNewDynMsgAllApi(new HttpOnNextListener() {
                @Override
                public void onNext(Object o) {
                    MineApp.mineNewsCount.setFriendDynamicGiftNum(0);
                    MineApp.mineNewsCount.setFriendDynamicGoodNum(0);
                    MineApp.mineNewsCount.setFriendDynamicReviewNum(0);
                    mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
                    if (MineApp.mineNewsCount.getSystemNewsNum() > 0) {
                        clearAllHistoryMsg(BaseActivity.systemUserId);
                    }
                    SCToastUtil.showToast(activity, "已全部清除", true);
                }
            }, activity).setReviewType(0);
            HttpManager.getInstance().doHttpDeal(api);
        });
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

    /**
     * 对方的阿里百川账号
     */
    private void otherImAccountInfoApi() {
        otherImAccountInfoApi api = new otherImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                otherIMUserId = o.getImUserId();
                mConversationService = loginHelper.getConversationService();
                conversation = mConversationService.getConversationByUserId(otherIMUserId, LoginSampleHelper.APP_KEY);
            }
        }, activity);
        api.setOtherUserId(BaseActivity.systemUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void clearAllHistoryMsg(long otherUserId) {
        clearAllHistoryMsgApi api = new clearAllHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.mineNewsCount.setSystemNewsNum(0);
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
            }
        }, activity).setOtherUserId(otherUserId).setMsgChannelType(1).setDriftBottleId(0);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
