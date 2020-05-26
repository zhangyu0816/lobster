package com.zb.module_mine.vm;

import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.chatListApi;
import com.zb.lib_base.api.newDynMsgAllNumApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.MineNewsCount;
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

    }

    @Override
    public void newDynMsgAllNum() {
        newDynMsgAllNumApi api = new newDynMsgAllNumApi(new HttpOnNextListener<MineNewsCount>() {
            @Override
            public void onNext(MineNewsCount o) {
                mineNewsCount = o;
                mBinding.setVariable(BR.viewModel, NewsManagerViewModel.this);
                chatList();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void chatList() {
        chatListApi api = new chatListApi(new HttpOnNextListener<List<ChatList>>() {
            @Override
            public void onNext(List<ChatList> o) {
                for (ChatList chatList : o) {
                    if (chatList.getUserId() == BaseActivity.systemUserId) {
                        mineNewsCount.setSystemNewsNum(chatList.getNoReadNum());
                        mBinding.setVariable(BR.viewModel, NewsManagerViewModel.this);
                        mBinding.setVariable(BR.newsDate, chatList.getCreationDate());
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
