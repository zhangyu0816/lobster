package com.zb.module_mine.vm;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.selfFeedBackApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineFeedbackBinding;
import com.zb.module_mine.iv.FeedbackVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class FeedbackViewModel extends BaseViewModel implements FeedbackVMInterface, OnRefreshListener, OnLoadMoreListener {
    public MineAdapter adapter;
    public MineInfo mineInfo;
    private List<FeedbackInfo> feedbackInfoList = new ArrayList<>();
    private int pageNo = 1;
    private MineFeedbackBinding mBinding;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineFeedbackBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_feedback, feedbackInfoList, this);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        selfFeedBack();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        feedbackInfoList.clear();
        adapter.notifyDataSetChanged();
        selfFeedBack();
    }

    @Override
    public void selfFeedBack() {
        selfFeedBackApi api = new selfFeedBackApi(new HttpOnNextListener<List<FeedbackInfo>>() {
            @Override
            public void onNext(List<FeedbackInfo> o) {
                int start = feedbackInfoList.size();
                feedbackInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, feedbackInfoList.size());
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
        }, activity).setPageNumber(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void clickItem(int position) {
        ActivityUtils.getMineFeedbackDetail(feedbackInfoList.get(position));
    }

    @Override
    public void add(View view) {
        ActivityUtils.getMineFeedbackDetail(new FeedbackInfo());
    }

}
