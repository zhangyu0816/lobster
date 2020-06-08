package com.zb.module_card.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.personOtherDynApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.CardMemberDiscoverBinding;
import com.zb.module_card.iv.MemberDiscoverVMInterface;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class MemberDiscoverViewModel extends BaseViewModel implements MemberDiscoverVMInterface, OnRefreshListener, OnLoadMoreListener {

    public CardAdapter adapter;
    private AreaDb areaDb;
    private int pageNo = 1;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private CardMemberDiscoverBinding discoverBinding;
    private BaseReceiver publishReceiver;
    public long otherUserId;
    private MineInfo mineInfo;
    private MemberInfo memberInfo;
    private int prePosition = -1;
    private BaseReceiver attentionReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        discoverBinding = (CardMemberDiscoverBinding) binding;
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
    }

    public void onDestroy() {
        publishReceiver.unregisterReceiver();
        attentionReceiver.unregisterReceiver();
    }

    @Override
    public void setAdapter() {
        adapter = new CardAdapter<>(activity, R.layout.item_card_discover, discoverInfoList, this);
        if (otherUserId == 0)
            getData();
        else {
            if (otherUserId == BaseActivity.userId) {
                mineInfo = mineInfoDb.getMineInfo();
                getData();
            } else {
                otherInfo();
            }
        }
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
                discoverBinding.noNetLinear.setVisibility(View.GONE);
                int start = discoverInfoList.size();
                discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                discoverBinding.refresh.finishRefresh();
                discoverBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    discoverBinding.noNetLinear.setVisibility(View.VISIBLE);
                    discoverBinding.refresh.setEnableLoadMore(false);
                    discoverBinding.refresh.finishRefresh();
                    discoverBinding.refresh.finishLoadMore();
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    discoverBinding.refresh.setEnableLoadMore(false);
                    discoverBinding.refresh.finishRefresh();
                    discoverBinding.refresh.finishLoadMore();
                }
            }
        }, activity)
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDynType(1)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void personOtherDyn() {
        personOtherDynApi api = new personOtherDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                discoverBinding.noNetLinear.setVisibility(View.GONE);
                int start = discoverInfoList.size();
                for (DiscoverInfo item : o) {
                    if (otherUserId == BaseActivity.userId) {
                        item.setNick(mineInfo.getNick());
                        item.setImage(mineInfo.getImage());
                    } else {
                        item.setNick(memberInfo.getNick());
                        item.setImage(memberInfo.getImage());
                    }
                }
                discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                discoverBinding.refresh.finishRefresh();
                discoverBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    discoverBinding.noNetLinear.setVisibility(View.VISIBLE);
                    discoverBinding.refresh.setEnableLoadMore(false);
                    discoverBinding.refresh.finishRefresh();
                    discoverBinding.refresh.finishLoadMore();
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    discoverBinding.refresh.setEnableLoadMore(false);
                    discoverBinding.refresh.finishRefresh();
                    discoverBinding.refresh.finishLoadMore();
                }
            }
        }, activity)
                .setDynType(2)
                .setOtherUserId(otherUserId)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                getData();
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onRefreshForNet(View view) {
        // 下拉刷新
        discoverBinding.refresh.setEnableLoadMore(true);
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
