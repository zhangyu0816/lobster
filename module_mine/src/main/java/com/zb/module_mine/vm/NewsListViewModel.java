package com.zb.module_mine.vm;

import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.dynNewMsgListApi;
import com.zb.lib_base.api.readNewDynMsgAllApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.MineNews;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineNewsListBinding;
import com.zb.module_mine.iv.NewsListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NewsListViewModel extends BaseViewModel implements NewsListVMInterface, OnRefreshListener, OnLoadMoreListener {
    public MineAdapter adapter;
    private List<MineNews> mineNewsList = new ArrayList<>();
    public int reviewType;
    private int pageNo = 1;
    private MineNewsListBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineNewsListBinding) binding;
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_mine_news_list, mineNewsList, this);
        dynNewMsgList();
        readNewDynMsgAll();
    }

    @Override
    public void dynNewMsgList() {
        dynNewMsgListApi api = new dynNewMsgListApi(new HttpOnNextListener<List<MineNews>>() {
            @Override
            public void onNext(List<MineNews> o) {
                int start = mineNewsList.size();
                mineNewsList.addAll(o);
                adapter.notifyItemRangeChanged(start, mineNewsList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                mBinding.setNoData(false);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo).setReviewType(reviewType);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void clickDyn(MineNews mineNews) {
        if (mineNews.getFriendDynamicDycType() >= 4)
            ActivityUtils.getHomeDiscoverVideo(mineNews.getFriendDynamicId());
        else
            ActivityUtils.getHomeDiscoverDetail(mineNews.getFriendDynamicId());
    }

    @Override
    public void toMember(MineNews mineNews) {
        ActivityUtils.getCardMemberDetail(mineNews.getReviewUserId(),false);
    }

    @Override
    public void readNewDynMsgAll() {
        readNewDynMsgAllApi api = new readNewDynMsgAllApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                //1评论  2.点赞 3.礼物
                if (reviewType == 1)
                    MineApp.mineNewsCount.setFriendDynamicReviewNum(0);
                else if (reviewType == 2)
                    MineApp.mineNewsCount.setFriendDynamicGoodNum(0);
                else
                    MineApp.mineNewsCount.setFriendDynamicGiftNum(0);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_newsCount"));
            }
        }, activity).setReviewType(reviewType);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        dynNewMsgList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        mineNewsList.clear();
        adapter.notifyDataSetChanged();
        dynNewMsgList();
    }
}
