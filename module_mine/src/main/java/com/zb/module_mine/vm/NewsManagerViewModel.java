package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.api.systemChatApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.NewsManagerVMInterface;

import java.util.List;

public class NewsManagerViewModel extends BaseViewModel implements NewsManagerVMInterface {
    public MineNewsCount mineNewsCount;

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        readNewDynMsgAllApi api = new readNewDynMsgAllApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);

        if (mineNewsCount.getSystemNewsNum() > 0) {
            systemHistoryMsgList();
        }
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
    public void newDynMsgAllNum() {
        newDynMsgAllNumApi api = new newDynMsgAllNumApi(new HttpOnNextListener<MineNewsCount>() {
            @Override
            public void onNext(MineNewsCount o) {
                mineNewsCount = o;
                systemChat();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void systemChat() {
        systemChatApi api = new systemChatApi(new HttpOnNextListener<SystemMsg>() {
            @Override
            public void onNext(SystemMsg o) {
                mineNewsCount.setSystemNewsNum(o.getNoReadNum());
                mBinding.setVariable(BR.viewModel, NewsManagerViewModel.this);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mineNewsCount.setSystemNewsNum(0);
                    mBinding.setVariable(BR.viewModel, NewsManagerViewModel.this);
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
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
                mineNewsCount.setSystemNewsNum(0);
                mBinding.setVariable(BR.viewModel, NewsManagerViewModel.this);
            }
        }, activity).setMessageId(messageId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
