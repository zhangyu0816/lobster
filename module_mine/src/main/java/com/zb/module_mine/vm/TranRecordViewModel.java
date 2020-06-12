package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.tranRecordsApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.TranRecord;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineTranRecordBinding;
import com.zb.module_mine.iv.TranRecordVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class TranRecordViewModel extends BaseViewModel implements TranRecordVMInterface, OnRefreshListener, OnLoadMoreListener {
    public int tranType;
    public MineAdapter adapter;
    private List<TranRecord> tranRecordList = new ArrayList<>();
    private int pageNo = 1;
    private MineTranRecordBinding mBinding;
    private BaseReceiver finishRefreshReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineTranRecordBinding) binding;
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
        adapter = new MineAdapter<>(activity, R.layout.item_tran_record, tranRecordList, this);
        tranRecords();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        tranRecords();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        tranRecordList.clear();
        adapter.notifyDataSetChanged();
        tranRecords();
    }

    @Override
    public void tranRecords() {
        tranRecordsApi api = new tranRecordsApi(new HttpOnNextListener<List<TranRecord>>() {
            @Override
            public void onNext(List<TranRecord> o) {
                int start = tranRecordList.size();
                tranRecordList.addAll(o);
                adapter.notifyItemRangeChanged(start, tranRecordList.size());
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
        }, activity).setPageNo(pageNo).setTranType(tranType);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
