package com.zb.module_home.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.HomeAdapter;
import com.zb.module_home.R;
import com.zb.module_home.iv.FollowVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FollowViewModel extends BaseViewModel implements FollowVMInterface, OnRefreshListener, OnLoadMoreListener {
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
        discoverInfoList.add(new DiscoverInfo());
        refreshLayout.finishLoadMore();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
    }

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }

    @Override
    public void clickItem(int position) {
        super.clickItem(position);
        SCToastUtil.showToast(activity,position+"");
    }
}
