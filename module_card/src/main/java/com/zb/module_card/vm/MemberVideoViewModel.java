package com.zb.module_card.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardMemberVideoBinding;
import com.zb.module_card.iv.MemberVideoVMInterface;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class MemberVideoViewModel extends BaseViewModel implements MemberVideoVMInterface, OnRefreshListener, OnLoadMoreListener {
    public CardAdapter adapter;
    public long otherUserId;
    private AreaDb areaDb;
    private int pageNo = 1;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private CardMemberVideoBinding mBinding;
    private BaseReceiver publishReceiver;
    private int prePosition = -1;
    private BaseReceiver attentionReceiver;
    private BaseReceiver finishRefreshReceiver;
    private BaseReceiver mainSelectReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        mBinding = (CardMemberVideoBinding) binding;
        publishReceiver = new BaseReceiver(activity, "lobster_publish") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefreshForNet(null);
            }
        };
        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (prePosition == -1) return;
                int goodNum = intent.getIntExtra("goodNum", 0);
                discoverInfoList.get(prePosition).setGoodNum(goodNum);
                adapter.notifyItemChanged(prePosition);
                prePosition = -1;
            }
        };
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };
        mainSelectReceiver = new BaseReceiver(activity, "lobster_mainSelect") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.setEnableLoadMore(true);
                pageNo = 1;
                discoverInfoList.clear();
                adapter.notifyDataSetChanged();
                personOtherDyn();
            }
        };
    }

    public void onDestroy() {
        publishReceiver.unregisterReceiver();
        attentionReceiver.unregisterReceiver();
        finishRefreshReceiver.unregisterReceiver();
        mainSelectReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        adapter = new CardAdapter<>(activity, R.layout.item_card_video, discoverInfoList, this);
        getData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        getData();
    }

    private void getData() {
        if (otherUserId == 0) {
            dynPiazzaList();
        } else {
            personOtherDyn();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefreshForNet(null);
    }

    @Override
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.noNetLinear.setVisibility(View.GONE);
                int start = discoverInfoList.size();
                discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.noNetLinear.setVisibility(View.VISIBLE);
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity)
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDynType(2)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void personOtherDyn() {
        personOtherDynApi api = new personOtherDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                mBinding.noNetLinear.setVisibility(View.GONE);
                int start = discoverInfoList.size();
                discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    mBinding.noNetLinear.setVisibility(View.VISIBLE);
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity)
                .setDynType(3)
                .setOtherUserId(otherUserId)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onRefreshForNet(View view) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        discoverInfoList.clear();
        adapter.notifyDataSetChanged();
        getData();
    }

    @Override
    public void clickItem(int position) {
        prePosition = position;
        DiscoverInfo discoverInfo = discoverInfoList.get(position);
        if (discoverInfo.getVideoUrl().isEmpty())
            ActivityUtils.getHomeDiscoverDetail(discoverInfo.getFriendDynId());
        else
            ActivityUtils.getHomeDiscoverVideo(discoverInfo.getFriendDynId());
    }
}
