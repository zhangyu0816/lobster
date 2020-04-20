package com.zb.module_bottle.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.iv.BottleListVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class BottleListViewModel extends BaseViewModel implements BottleListVMInterface, OnRefreshListener, OnLoadMoreListener {

    public BottleAdapter adapter;
    private List<BottleInfo> bottleInfoList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        for (int i = 0; i < 10; i++) {
            bottleInfoList.add(new BottleInfo());
        }
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle, bottleInfoList, this);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
//            int start = list.size();
//            list.addAll(newItems);
//            adapter.notifyItemInserted(start, list.size());
        bottleInfoList.add(new BottleInfo());
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


}
