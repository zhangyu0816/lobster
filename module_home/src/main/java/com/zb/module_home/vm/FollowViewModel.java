package com.zb.module_home.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.attentionDynApi;
import com.zb.lib_base.db.CollectIDDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeFollowBinding;
import com.zb.module_home.iv.FollowVMInterface;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class FollowViewModel extends BaseViewModel implements FollowVMInterface, OnRefreshListener, OnLoadMoreListener {
    public HomeAdapter adapter;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private int pageNo = 1;
    private HomeFollowBinding followBinding;
    private BaseReceiver publishReceiver;

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_discover, discoverInfoList, this);
        attentionDyn();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        followBinding = (HomeFollowBinding) binding;
        publishReceiver = new BaseReceiver(activity, "lobster_publish") {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRefreshForNet(null);
            }
        };
    }

    public void onDestroy() {
        publishReceiver.unregisterReceiver();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        attentionDyn();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefreshForNet(null);
    }

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }

    @Override
    public void entryBottle(View view) {
        ActivityUtils.getBottleMain();
    }

    @Override
    public void attentionDyn() {
        attentionDynApi api = new attentionDynApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                followBinding.noNetLinear.setVisibility(View.GONE);
                int start = discoverInfoList.size();
                discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                followBinding.refresh.finishRefresh();
                followBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    followBinding.noNetLinear.setVisibility(View.VISIBLE);
                    followBinding.refresh.setEnableLoadMore(false);
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    followBinding.refresh.setEnableLoadMore(false);
                }
            }
        }, activity).setPageNo(pageNo).setTimeSortType(1);
        api.setShowProgress(pageNo == 1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onRefreshForNet(View view) {
        // 下拉刷新
        followBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        discoverInfoList.clear();
        attentionDyn();
    }

}
