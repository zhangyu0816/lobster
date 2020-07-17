package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearAllHistoryMsgApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.NewsManagerVMInterface;

import androidx.databinding.ViewDataBinding;

public class NewsManagerViewModel extends BaseViewModel implements NewsManagerVMInterface {
    private BaseReceiver newsCountReceiver;
    private LoginSampleHelper loginHelper;

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
    }

    @Override
    public void back(View view) {
        super.back(view);
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
                        // 获取与某个聊天对象的会话记录
                        otherImAccountInfoApi();
                        clearAllHistoryMsg(BaseActivity.systemUserId);
//                        thirdReadChat(BaseActivity.systemUserId);
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
                IYWConversationService mConversationService = loginHelper.getConversationService();
                YWConversation conversation = mConversationService.getConversationByUserId(o.getImUserId(), LoginSampleHelper.APP_KEY);
                try {
                    // 删除所有聊天记录
                    conversation.getMessageLoader().deleteAllMessage();
                } catch (Exception e) {
                }
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
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
