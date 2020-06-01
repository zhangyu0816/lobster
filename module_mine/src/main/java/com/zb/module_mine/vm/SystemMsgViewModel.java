package com.zb.module_mine.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineSystemMsgBinding;
import com.zb.module_mine.iv.SystemMsgVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class SystemMsgViewModel extends BaseViewModel implements SystemMsgVMInterface, OnRefreshListener, OnLoadMoreListener {
    public MineAdapter adapter;
    private List<SystemMsg> systemMsgList = new ArrayList<>();
    private int pageNo = 1;
    private MineSystemMsgBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineSystemMsgBinding) binding;
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_system_msg, systemMsgList, this);
        systemHistoryMsgList();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        systemHistoryMsgList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        systemMsgList.clear();
        adapter.notifyDataSetChanged();
        systemHistoryMsgList();
    }

    @Override
    public void systemHistoryMsgList() {
        systemHistoryMsgListApi api = new systemHistoryMsgListApi(new HttpOnNextListener<List<SystemMsg>>() {
            @Override
            public void onNext(List<SystemMsg> o) {
                int start = systemMsgList.size();
                systemMsgList.addAll(o);
                adapter.notifyItemRangeChanged(start, systemMsgList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                if (pageNo == 1)
                    clearHistoryMsg(o.get(0).getId());
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

    @Override
    public void clearHistoryMsg(long messageId) {
        clearHistoryMsgApi api = new clearHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
            }
        }, activity).setMessageId(messageId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
