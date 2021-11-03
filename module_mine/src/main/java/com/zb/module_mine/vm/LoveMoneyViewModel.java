package com.zb.module_mine.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.rewardsOrderListApi;
import com.zb.lib_base.api.statisticsRewardsCountApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.LoveMoney;
import com.zb.lib_base.model.PersonInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.AcLoveMoneyBinding;
import com.zb.module_mine.windows.OpenLovePW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class LoveMoneyViewModel extends BaseViewModel implements OnRefreshListener, OnLoadMoreListener {
    private AcLoveMoneyBinding mBinding;
    public MineAdapter<LoveMoney> adapter;
    private int pageNo = 1;
    private List<LoveMoney> loveMoneyList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveMoneyBinding) binding;
        mBinding.setTitle("我的收益");
        setAdapter();
        statisticsRewardsCount();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        rewardsOrderList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        loveMoneyList.clear();
        adapter.notifyDataSetChanged();
        rewardsOrderList();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_love_money_reward, loveMoneyList, this);
        rewardsOrderList();
    }

    public void withdraw(View view) {
    }

    public void openLove(View view) {
        new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
    }

    private void statisticsRewardsCount() {
        statisticsRewardsCountApi api = new statisticsRewardsCountApi(new HttpOnNextListener<PersonInfo>() {
            @Override
            public void onNext(PersonInfo o) {
                mBinding.setMoney(o.getTotalRewards());
            }
        }, activity).setTranStatusType(200);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void rewardsOrderList() {
        rewardsOrderListApi api = new rewardsOrderListApi(new HttpOnNextListener<List<LoveMoney>>() {
            @Override
            public void onNext(List<LoveMoney> o) {
                int start = loveMoneyList.size();
                loveMoneyList.addAll(o);
                adapter.notifyItemRangeChanged(start, loveMoneyList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

}
