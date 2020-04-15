package com.zb.module_home.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.HomeAdapter;
import com.zb.module_home.R;
import com.zb.module_home.iv.RecommendVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class RecommendViewModel extends BaseViewModel implements RecommendVMInterface, OnRefreshListener, OnLoadMoreListener {
    public HomeAdapter adapter;

    private List<DiscoverInfo> discoverInfoList = new ArrayList<>();

    @Override
    public void setAdapter() {
        for (int i = 0; i < 10; i++) {
            discoverInfoList.add(new DiscoverInfo());
        }
        adapter = new HomeAdapter<>(activity, R.layout.item_discover, discoverInfoList, this);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
//            int start = list.size();
//            list.addAll(newItems);
//            adapter.notifyItemInserted(start, list.size());
        discoverInfoList.add(new DiscoverInfo());
        refreshLayout.finishLoadMore();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
//            list.clear();
//            list.addAll(newList);
//            adapter.notifyItemRangeChanged(0, list.size());
        refreshLayout.finishRefresh();
    }

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }
}
