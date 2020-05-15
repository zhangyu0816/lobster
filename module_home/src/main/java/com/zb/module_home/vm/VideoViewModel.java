package com.zb.module_home.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeVideoBinding;
import com.zb.module_home.iv.VideoVMInterface;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class VideoViewModel extends BaseViewModel implements VideoVMInterface, OnRefreshListener, OnLoadMoreListener {
    public HomeAdapter adapter;
    private AreaDb areaDb;
    private int pageNo = 1;
    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();
    private HomeVideoBinding videoBinding;
    private BaseReceiver publishReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        areaDb = new AreaDb(Realm.getDefaultInstance());
        videoBinding = (HomeVideoBinding) binding;
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
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_home_video, discoverInfoList, this);
        dynPiazzaList();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        dynPiazzaList();
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
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                videoBinding.noNetLinear.setVisibility(View.GONE);
                int start = discoverInfoList.size();
                discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, discoverInfoList.size());
                videoBinding.refresh.finishRefresh();
                videoBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectException) {
                    videoBinding.noNetLinear.setVisibility(View.VISIBLE);
                    videoBinding.refresh.setEnableLoadMore(false);
                } else if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    videoBinding.refresh.setEnableLoadMore(false);
                }
            }
        }, activity)
                .setCityId(areaDb.getCityId(MineApp.cityName))
                .setDynType(2)
                .setOtherUserId(0)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void onRefreshForNet(View view) {
        // 下拉刷新
        videoBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        discoverInfoList.clear();
        dynPiazzaList();
    }
}
