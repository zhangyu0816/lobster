package com.zb.module_home.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.seeGiftRewardsApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeRewardListBinding;
import com.zb.module_home.iv.RewardListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;


public class RewardListViewModel extends BaseViewModel implements RewardListVMInterface, OnRefreshListener, OnLoadMoreListener {

    public HomeAdapter adapter;
    public long friendDynId;
    private List<Reward> rewardList = new ArrayList<>();
    private int pageNo = 1;
    private HomeRewardListBinding mBinding;
    private BaseReceiver finishRefreshReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeRewardListBinding) binding;
        setAdapter();
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };
    }

    public void onDestroy() {
        finishRefreshReceiver.unregisterReceiver();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_reward_ranking, rewardList, this);
        seeGiftRewards();
    }

    @Override
    public void seeGiftRewards() {
        seeGiftRewardsApi api = new seeGiftRewardsApi(new HttpOnNextListener<List<Reward>>() {
            @Override
            public void onNext(List<Reward> o) {
                mBinding.setNoData(false);
                int start = rewardList.size();
                rewardList.addAll(o);
                adapter.notifyItemRangeChanged(start, rewardList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                    if (rewardList.size() == 0) {
                        mBinding.setNoData(true);
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId)
                .setRewardSortType(2)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(Reward reward) {
        ActivityUtils.getCardMemberDetail(reward.getUserId(),false);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        seeGiftRewards();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        rewardList.clear();
        adapter.notifyDataSetChanged();
        seeGiftRewards();
    }
}
